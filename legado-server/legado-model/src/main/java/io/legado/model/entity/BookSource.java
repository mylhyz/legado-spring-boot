package io.legado.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 书源实体
 */
@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "book_sources",
       indexes = {
           @Index(name = "idx_sources_name", columnList = "source_name"),
           @Index(name = "idx_sources_enabled", columnList = "enabled")
       })
public class BookSource {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 书源名称
     */
    @Column(name = "source_name", nullable = false, length = 256)
    private String sourceName;
    
    /**
     * 书源URL
     */
    @Column(name = "source_url", nullable = false, length = 2048)
    private String sourceUrl;
    
    /**
     * 书源图标
     */
    @Column(name = "source_icon", length = 2048)
    private String sourceIcon;
    
    /**
     * 书源分组
     */
    @Column(name = "source_group", length = 256)
    private String sourceGroup;
    
    /**
     * 是否启用
     */
    @Column(name = "enabled")
    private Boolean enabled = true;
    
    /**
     * 是否启用发现
     */
    @Column(name = "enabled_explore")
    private Boolean enabledExplore = true;
    
    /**
     * 权重
     */
    @Column(name = "weight")
    private Integer weight = 0;
    
    /**
     * 自定义排序
     */
    @Column(name = "custom_order")
    private Integer customOrder = 0;
    
    /**
     * 登录URL
     */
    @Column(name = "login_url", length = 2048)
    private String loginUrl;
    
    /**
     * 登录UI
     */
    @Column(name = "login_ui", columnDefinition = "TEXT")
    private String loginUi;
    
    /**
     * 登录检查JS
     */
    @Column(name = "login_check_js", columnDefinition = "TEXT")
    private String loginCheckJs;
    
    /**
     * 书籍URL匹配规则
     */
    @Column(name = "book_url_pattern", length = 1024)
    private String bookUrlPattern;
    
    /**
     * 请求头
     */
    @Column(name = "header", columnDefinition = "TEXT")
    private String header;
    
    /**
     * 搜索URL
     */
    @Column(name = "search_url", length = 2048)
    private String searchUrl;
    
    /**
     * 发现URL
     */
    @Column(name = "explore_url", columnDefinition = "TEXT")
    private String exploreUrl;
    
    /**
     * 发现筛选规则
     */
    @Column(name = "explore_screen", columnDefinition = "TEXT")
    private String exploreScreen;
    
    /**
     * 发现规则（JSON格式）
     */
    @Column(name = "rule_explore", columnDefinition = "TEXT")
    private String ruleExplore;
    
    /**
     * 搜索规则（JSON格式）
     */
    @Column(name = "rule_search", columnDefinition = "TEXT")
    private String ruleSearch;
    
    /**
     * 书籍信息规则（JSON格式）
     */
    @Column(name = "rule_book_info", columnDefinition = "TEXT")
    private String ruleBookInfo;
    
    /**
     * 目录规则（JSON格式）
     */
    @Column(name = "rule_toc", columnDefinition = "TEXT")
    private String ruleToc;
    
    /**
     * 正文规则（JSON格式）
     */
    @Column(name = "rule_content", columnDefinition = "TEXT")
    private String ruleContent;
    
    /**
     * 书评规则（JSON格式）
     */
    @Column(name = "rule_review", columnDefinition = "TEXT")
    private String ruleReview;
    
    /**
     * 最后更新时间
     */
    @Column(name = "last_update_time")
    private Long lastUpdateTime;
    
    /**
     * 响应时间
     */
    @Column(name = "respond_time")
    private Long respondTime;
    
    /**
     * 内容替换规则
     */
    @Column(name = "content_replace_rule", columnDefinition = "TEXT")
    private String contentReplaceRule;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
}
