package io.legado.core.service;

import io.legado.model.entity.BookSource;
import io.legado.model.repository.BookSourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 书源服务
 */
@Slf4j
@Service
public class BookSourceService {
    
    @Autowired
    private BookSourceRepository bookSourceRepository;
    
    /**
     * 获取所有书源
     */
    public List<BookSource> getAllSources() {
        return bookSourceRepository.findAll();
    }
    
    /**
     * 获取启用的书源
     */
    public List<BookSource> getEnabledSources() {
        return bookSourceRepository.findByEnabledTrueOrderByWeightDesc();
    }
    
    /**
     * 根据ID获取书源
     */
    public BookSource getSourceById(Long id) {
        return bookSourceRepository.findById(id).orElse(null);
    }
    
    /**
     * 添加书源
     */
    @Transactional
    public BookSource addSource(BookSource source) {
        return bookSourceRepository.save(source);
    }
    
    /**
     * 更新书源
     */
    @Transactional
    public BookSource updateSource(Long id, BookSource source) {
        BookSource existing = bookSourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("书源不存在"));
        
        source.setId(id);
        return bookSourceRepository.save(source);
    }
    
    /**
     * 删除书源
     */
    @Transactional
    public void deleteSource(Long id) {
        bookSourceRepository.deleteById(id);
    }
    
    /**
     * 批量保存书源
     */
    @Transactional
    public void saveSources(List<BookSource> sources) {
        bookSourceRepository.saveAll(sources);
    }
    
    /**
     * 启用/禁用书源
     */
    @Transactional
    public void toggleSource(Long id, Boolean enabled) {
        BookSource source = bookSourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("书源不存在"));
        
        source.setEnabled(enabled);
        bookSourceRepository.save(source);
    }
    
}
