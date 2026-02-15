package io.legado.api.controller;

import io.legado.api.dto.ApiResponse;
import io.legado.core.service.BookSourceService;
import io.legado.model.entity.BookSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 书源API控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/sources")
public class BookSourceController {
    
    @Autowired
    private BookSourceService bookSourceService;
    
    /**
     * 获取所有书源
     */
    @GetMapping
    public ApiResponse<List<BookSource>> getAllSources() {
        List<BookSource> sources = bookSourceService.getAllSources();
        return ApiResponse.success(sources);
    }
    
    /**
     * 获取书源详情
     */
    @GetMapping("/{id}")
    public ApiResponse<BookSource> getSourceById(@PathVariable Long id) {
        BookSource source = bookSourceService.getSourceById(id);
        if (source == null) {
            return ApiResponse.error(404, "书源不存在");
        }
        return ApiResponse.success(source);
    }
    
    /**
     * 添加书源
     */
    @PostMapping
    public ApiResponse<BookSource> addSource(@RequestBody BookSource source) {
        BookSource saved = bookSourceService.addSource(source);
        return ApiResponse.success(saved);
    }
    
    /**
     * 批量添加书源
     */
    @PostMapping("/batch")
    public ApiResponse<Void> addSources(@RequestBody List<BookSource> sources) {
        bookSourceService.saveSources(sources);
        return ApiResponse.success(null);
    }
    
    /**
     * 从URL导入书源
     */
    @PostMapping("/import/url")
    public ApiResponse<Integer> importFromUrl(@RequestParam String url) {
        try {
            int count = bookSourceService.importFromUrl(url);
            return ApiResponse.success(count);
        } catch (IOException e) {
            log.error("从URL导入书源失败: {}", url, e);
            return ApiResponse.error(400, "从URL导入书源失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新书源
     */
    @PutMapping("/{id}")
    public ApiResponse<BookSource> updateSource(
            @PathVariable Long id,
            @RequestBody BookSource source) {
        BookSource updated = bookSourceService.updateSource(id, source);
        return ApiResponse.success(updated);
    }
    
    /**
     * 删除书源
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteSource(@PathVariable Long id) {
        bookSourceService.deleteSource(id);
        return ApiResponse.success(null);
    }
    
    /**
     * 启用/禁用书源
     */
    @PutMapping("/{id}/toggle")
    public ApiResponse<Void> toggleSource(
            @PathVariable Long id,
            @RequestParam Boolean enabled) {
        bookSourceService.toggleSource(id, enabled);
        return ApiResponse.success(null);
    }
    
}
