package io.legado.model.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * RSS文章实体
 */
@Data
@Entity
@Table(name = "rss_articles",
       indexes = {
           @Index(name = "idx_rss_articles_source", columnList = "source_id")
       })
public class RssArticle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 源ID
     */
    @Column(name = "source_id", nullable = false)
    private Long sourceId;
    
    /**
     * 标题
     */
    @Column(name = "title", nullable = false, length = 512)
    private String title;
    
    /**
     * 描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /**
     * 内容
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    /**
     * 链接
     */
    @Column(name = "link", length = 2048)
    private String link;
    
    /**
     * 发布日期
     */
    @Column(name = "pub_date")
    private Long pubDate;
    
    /**
     * 图片
     */
    @Column(name = "image", length = 2048)
    private String image;
    
    /**
     * 是否已读
     */
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
}
