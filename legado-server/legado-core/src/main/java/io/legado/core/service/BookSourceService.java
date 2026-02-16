package io.legado.core.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.legado.model.entity.BookSource;
import io.legado.model.repository.BookSourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 书源服务
 */
@Slf4j
@Service
public class BookSourceService {
    
    @Autowired
    private BookSourceRepository bookSourceRepository;
    
    @Autowired
    private io.legado.core.utils.HttpClient httpClient;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
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
    
    /**
     * 从URL导入书源
     */
    @Transactional
    public int importFromUrl(String url) throws IOException {
        String jsonContent = httpClient.get(url);
        List<Map<String, Object>> rawSources = objectMapper.readValue(jsonContent, new TypeReference<List<Map<String, Object>>>() {});
        
        List<BookSource> sources = rawSources.stream().map(this::convertToBookSource).toList();
        bookSourceRepository.saveAll(sources);
        return sources.size();
    }
    
    private BookSource convertToBookSource(Map<String, Object> raw) {
        BookSource source = new BookSource();
        
        source.setSourceName((String) raw.getOrDefault("bookSourceName", ""));
        source.setSourceUrl((String) raw.getOrDefault("bookSourceUrl", ""));
        source.setSourceIcon((String) raw.get("bookSourceIcon"));
        source.setSourceGroup((String) raw.get("bookSourceGroup"));
        source.setEnabled(raw.get("enabled") != null ? (Boolean) raw.get("enabled") : true);
        source.setEnabledExplore(raw.get("enabledExplore") != null ? (Boolean) raw.get("enabledExplore") : true);
        source.setWeight(raw.get("weight") != null ? ((Number) raw.get("weight")).intValue() : 0);
        source.setCustomOrder(raw.get("customOrder") != null ? ((Number) raw.get("customOrder")).intValue() : 0);
        source.setLoginUrl((String) raw.get("loginUrl"));
        source.setLoginUi((String) raw.get("loginUi"));
        source.setLoginCheckJs((String) raw.get("loginCheckJs"));
        source.setBookUrlPattern((String) raw.get("bookUrlPattern"));
        source.setHeader((String) raw.get("header"));
        source.setSearchUrl((String) raw.get("searchUrl"));
        source.setExploreUrl((String) raw.get("exploreUrl"));
        source.setLastUpdateTime(raw.get("lastUpdateTime") != null ? ((Number) raw.get("lastUpdateTime")).longValue() : null);
        source.setRespondTime(raw.get("respondTime") != null ? ((Number) raw.get("respondTime")).longValue() : null);
        
        source.setRuleSearch(serializeField(raw.get("ruleSearch")));
        source.setRuleBookInfo(serializeField(raw.get("ruleBookInfo")));
        source.setRuleToc(serializeField(raw.get("ruleToc")));
        source.setRuleContent(serializeField(raw.get("ruleContent")));
        source.setRuleReview(serializeField(raw.get("ruleReview")));
        source.setContentReplaceRule(serializeField(raw.get("contentReplaceRule")));
        source.setRuleExplore(serializeField(raw.get("ruleExplore")));
        
        return source;
    }
    
    private String serializeField(Object field) {
        if (field == null) return null;
        try {
            return objectMapper.writeValueAsString(field);
        } catch (IOException e) {
            log.warn("序列化字段失败", e);
            return null;
        }
    }
    
}
