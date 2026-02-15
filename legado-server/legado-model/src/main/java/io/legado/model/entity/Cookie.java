package io.legado.model.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Cookie存储实体
 */
@Data
@Entity
@Table(name = "cookies",
       indexes = {
           @Index(name = "idx_cookies_url", columnList = "url")
       })
public class Cookie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * URL
     */
    @Column(name = "url", nullable = false, unique = true, length = 2048)
    private String url;
    
    /**
     * Cookie值
     */
    @Column(name = "cookie", columnDefinition = "TEXT")
    private String cookie;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
}
