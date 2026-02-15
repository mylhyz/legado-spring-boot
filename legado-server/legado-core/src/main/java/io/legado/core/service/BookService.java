package io.legado.core.service;

import io.legado.core.booksource.BookSourceEngine;
import io.legado.core.utils.HttpClient;
import io.legado.model.entity.Book;
import io.legado.model.entity.BookChapter;
import io.legado.model.entity.BookSource;
import io.legado.model.repository.BookChapterRepository;
import io.legado.model.repository.BookRepository;
import io.legado.model.repository.BookSourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 书籍服务
 */
@Slf4j
@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private BookChapterRepository bookChapterRepository;
    
    @Autowired
    private BookSourceRepository bookSourceRepository;
    
    @Autowired
    private BookSourceEngine bookSourceEngine;
    
    @Autowired
    private HttpClient httpClient;
    
    /**
     * 获取所有书籍
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    /**
     * 根据ID获取书籍
     */
    @Cacheable(value = "book", key = "#id")
    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }
    
    /**
     * 根据URL获取书籍
     */
    public Book getBookByUrl(String bookUrl) {
        return bookRepository.findByBookUrl(bookUrl).orElse(null);
    }
    
    /**
     * 添加书籍
     */
    @Transactional
    public Book addBook(Book book) {
        // 检查是否已存在
        Optional<Book> existing = bookRepository.findByBookUrl(book.getBookUrl());
        if (existing.isPresent()) {
            log.info("书籍已存在: {}", book.getName());
            return existing.get();
        }
        
        return bookRepository.save(book);
    }
    
    /**
     * 从书源添加书籍
     */
    @Transactional
    public Book addBookFromSource(String bookUrl, String sourceUrl) {
        try {
            // 获取书源
            BookSource source = bookSourceRepository.findBySourceUrl(sourceUrl)
                    .orElseThrow(() -> new RuntimeException("书源不存在"));
            
            // 检查是否已存在
            Optional<Book> existing = bookRepository.findByBookUrl(bookUrl);
            if (existing.isPresent()) {
                log.info("书籍已存在: {}", existing.get().getName());
                return existing.get();
            }
            
            // 获取书籍详情
            Map<String, String> headers = parseHeaders(source.getHeader());
            String html = httpClient.get(bookUrl, headers);
            
            Book book = new Book();
            book.setBookUrl(bookUrl);
            book.setOrigin(sourceUrl);
            book.setOriginName(source.getSourceName());
            
            // 解析详情
            book = bookSourceEngine.parseBookInfo(html, source.getRuleBookInfo(), book);
            
            // 保存书籍
            book = bookRepository.save(book);
            
            // 获取章节列表
            loadChapters(book, source);
            
            return book;
            
        } catch (Exception e) {
            log.error("添加书籍失败: {}", bookUrl, e);
            throw new RuntimeException("添加书籍失败", e);
        }
    }
    
    /**
     * 加载章节列表
     */
    @Transactional
    public void loadChapters(Book book, BookSource source) {
        try {
            String tocUrl = book.getTocUrl() != null ? book.getTocUrl() : book.getBookUrl();
            Map<String, String> headers = parseHeaders(source.getHeader());
            String html = httpClient.get(tocUrl, headers);
            
            // 解析章节
            List<BookChapter> chapters = bookSourceEngine.parseToc(
                    html, 
                    source.getRuleToc(), 
                    book.getId(), 
                    source.getSourceUrl()
            );
            
            // 删除旧章节
            bookChapterRepository.deleteByBookId(book.getId());
            
            // 保存新章节
            bookChapterRepository.saveAll(chapters);
            
            // 更新书籍章节数
            book.setTotalChapterNum(chapters.size());
            bookRepository.save(book);
            
            log.info("加载章节完成: {}, 共{}章", book.getName(), chapters.size());
            
        } catch (Exception e) {
            log.error("加载章节失败: {}", book.getName(), e);
        }
    }
    
    /**
     * 获取章节列表
     */
    public List<BookChapter> getChapters(Long bookId) {
        return bookChapterRepository.findByBookIdOrderByChapterIndexAsc(bookId);
    }
    
    /**
     * 获取章节内容
     */
    @Cacheable(value = "chapterContent", key = "#bookId + ':' + #chapterIndex")
    public String getChapterContent(Long bookId, Integer chapterIndex) {
        BookChapter chapter = bookChapterRepository
                .findByBookIdAndChapterIndex(bookId, chapterIndex)
                .orElseThrow(() -> new RuntimeException("章节不存在"));
        
        // 如果已有内容，直接返回
        if (chapter.getContent() != null && !chapter.getContent().isEmpty()) {
            return chapter.getContent();
        }
        
        // 从书源获取内容
        Book book = getBookById(bookId);
        BookSource source = bookSourceRepository.findBySourceUrl(book.getOrigin())
                .orElseThrow(() -> new RuntimeException("书源不存在"));
        
        try {
            Map<String, String> headers = parseHeaders(source.getHeader());
            String html = httpClient.get(chapter.getUrl(), headers);
            
            // 解析内容
            String content = bookSourceEngine.parseContent(html, source.getRuleContent());
            
            // 保存内容
            chapter.setContent(content);
            bookChapterRepository.save(chapter);
            
            return content;
            
        } catch (Exception e) {
            log.error("获取章节内容失败: {} - 第{}章", book.getName(), chapterIndex, e);
            throw new RuntimeException("获取章节内容失败", e);
        }
    }
    
    /**
     * 更新阅读进度
     */
    @CacheEvict(value = "book", key = "#bookId")
    @Transactional
    public void updateProgress(Long bookId, Integer chapterIndex, Integer chapterPos) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("书籍不存在"));
        
        book.setDurChapterIndex(chapterIndex);
        book.setDurChapterPos(chapterPos);
        
        // 获取章节标题
        BookChapter chapter = bookChapterRepository
                .findByBookIdAndChapterIndex(bookId, chapterIndex)
                .orElse(null);
        
        if (chapter != null) {
            book.setDurChapterTitle(chapter.getTitle());
        }
        
        bookRepository.save(book);
    }
    
    /**
     * 删除书籍
     */
    @Transactional
    public void deleteBook(Long bookId) {
        // 删除章节
        bookChapterRepository.deleteByBookId(bookId);
        
        // 删除书籍
        bookRepository.deleteById(bookId);
        
        log.info("删除书籍: {}", bookId);
    }
    
    /**
     * 解析请求头
     */
    private Map<String, String> parseHeaders(String headerJson) {
        if (headerJson == null || headerJson.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(headerJson, new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            return java.util.Collections.emptyMap();
        }
    }
    
}
