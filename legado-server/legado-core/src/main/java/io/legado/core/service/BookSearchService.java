package io.legado.core.service;

import io.legado.core.booksource.BookSourceEngine;
import io.legado.core.dto.SearchResultDto;
import io.legado.core.utils.HttpClient;
import io.legado.model.entity.Book;
import io.legado.model.entity.BookSource;
import io.legado.model.repository.BookSourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * 书籍搜索服务
 */
@Slf4j
@Service
public class BookSearchService {
    
    @Autowired
    private BookSourceRepository bookSourceRepository;
    
    @Autowired
    private BookSourceEngine bookSourceEngine;
    
    @Autowired
    private HttpClient httpClient;
    
    @Autowired
    private Executor taskExecutor;
    
    /**
     * 搜索书籍
     * 
     * @param keyword 搜索关键词
     * @return 搜索结果列表
     */
    public List<SearchResultDto> search(String keyword) {
        // 获取所有启用的书源
        List<BookSource> sources = bookSourceRepository.findByEnabledTrueOrderByWeightDesc();
        
        log.info("开始搜索关键词: {}, 启用书源数量: {}", keyword, sources.size());
        
        // 并行搜索
        List<CompletableFuture<List<SearchResultDto>>> futures = sources.stream()
                .map(source -> searchFromSource(source, keyword))
                .collect(Collectors.toList());
        
        // 合并结果
        CompletableFuture<Void> allDone = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );
        
        CompletableFuture<List<SearchResultDto>> results = allDone.thenApply(v -> 
                futures.stream()
                        .flatMap(future -> future.join().stream())
                        .collect(Collectors.toList())
        );
        
        return results.join();
    }
    
    /**
     * 从单个书源搜索
     */
    @Async("taskExecutor")
    public CompletableFuture<List<SearchResultDto>> searchFromSource(BookSource source, String keyword) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 检查搜索URL
                if (source.getSearchUrl() == null || source.getSearchUrl().isEmpty()) {
                    return Collections.emptyList();
                }
                
                // 构建搜索URL
                String searchUrl = buildSearchUrl(source.getSearchUrl(), keyword);
                
                // 解析请求头
                Map<String, String> headers = parseHeaders(source.getHeader());
                
                // 发送请求
                String html = httpClient.get(searchUrl, headers);
                
                // 解析结果
                List<Book> books = bookSourceEngine.parseSearchResults(
                        html, 
                        source.getRuleSearch(), 
                        source.getSourceName(), 
                        source.getSourceUrl()
                );
                
                // 转换为DTO
                return books.stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
                
            } catch (Exception e) {
                log.warn("从书源 {} 搜索失败: {}", source.getSourceName(), e.getMessage());
                return Collections.emptyList();
            }
        }, taskExecutor);
    }
    
    /**
     * 构建搜索URL
     */
    private String buildSearchUrl(String searchUrl, String keyword) {
        // 替换关键词占位符
        return searchUrl.replace("{{key}}", keyword)
                       .replace("{{page}}", "1")
                       .replace("{key}", keyword)
                       .replace("{page}", "1");
    }
    
    /**
     * 解析请求头
     */
    private Map<String, String> parseHeaders(String headerJson) {
        if (headerJson == null || headerJson.isEmpty()) {
            return Collections.emptyMap();
        }
        
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(headerJson, new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            log.warn("解析请求头失败", e);
            return Collections.emptyMap();
        }
    }
    
    /**
     * 转换为DTO
     */
    private SearchResultDto convertToDto(Book book) {
        SearchResultDto dto = new SearchResultDto();
        dto.setName(book.getName());
        dto.setAuthor(book.getAuthor());
        dto.setIntro(book.getIntro());
        dto.setCoverUrl(book.getCoverUrl());
        dto.setBookUrl(book.getBookUrl());
        dto.setLatestChapterTitle(book.getLatestChapterTitle());
        dto.setWordCount(book.getWordCount());
        dto.setKind(book.getKind());
        dto.setSourceName(book.getOriginName());
        dto.setSourceUrl(book.getOrigin());
        return dto;
    }
    
}
