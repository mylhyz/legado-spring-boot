package io.legado.model.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * RSS订阅源实体
 */
@Data
@Entity
@Table(name = "rss_sources",
       indexes = {
           @Index(name = "idx_rss_sources_enabled", columnList = "enabled")
       })
public class RssSource {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 源名称
     */
    @Column(name = "source_name", nullable = false, length = 256)
    private String sourceName;
    
    /**
     * 源URL
     */
    @Column(name = "source_url", nullable = false, length = 2048)
    private String sourceUrl;
    
    /**
     * 源图标
     */
    @Column(name = "source_icon", length = 2048)
    private String sourceIcon;
    
    /**
     * 是否启用
     */
    @Column(name = "enabled")
    private Boolean enabled = true;
    
    /**
     * 排序规则
     */
    @Column(name = "rule_order", length = 512)
    private String ruleOrder;
    
    /**
     * 请求头
     */
    @Column(name = "header", columnDefinition = "TEXT")
    private String header;
    
    /**
     * 文章样式
     */
    @Column(name = "article_style")
    private Integer articleStyle = 0;
    
    /**
     * 是否使用BaseUrl加载
     */
    @Column(name = "load_with_base_url")
    private Boolean loadWithBaseUrl = false;
    
    /**
     * 文章规则（JSON格式）
     */
    @Column(name = "rule_articles", columnDefinition = "TEXT")
    private String ruleArticles;
    
    /**
     * 标题规则
     */
    @Column(name = "rule_title", length = 512)
    private String ruleTitle;
    
    /**
     * 发布日期规则
     */
    @Column(name = "rule_pub_date", length = 512)
    private String rulePubDate;
    
    /**
     * 描述规则
     */
    @Column(name = "rule_description", length = 512)
    private String ruleDescription;
    
    /**
     * 图片规则
     */
    @Column(name = "rule_image", length = 512)
    private String ruleImage;
    
    /**
     * 链接规则
     */
    @Column(name = "rule_link", length = 512)
    private String ruleLink;
    
    /**
     * 最后更新时间
     */
    @Column(name = "last_update_time")
    private Long lastUpdateTime;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
}
