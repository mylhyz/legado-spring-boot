package io.legado.model.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 替换规则实体
 */
@Data
@Entity
@Table(name = "replace_rules")
public class ReplaceRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 规则名称
     */
    @Column(name = "name", length = 256)
    private String name;
    
    /**
     * 匹配模式
     */
    @Column(name = "pattern", nullable = false, columnDefinition = "TEXT")
    private String pattern;
    
    /**
     * 替换内容
     */
    @Column(name = "replacement", columnDefinition = "TEXT")
    private String replacement;
    
    /**
     * 是否正则
     */
    @Column(name = "is_regex")
    private Boolean isRegex = true;
    
    /**
     * 作用范围
     */
    @Column(name = "scope", length = 512)
    private String scope;
    
    /**
     * 是否启用
     */
    @Column(name = "is_enabled")
    private Boolean isEnabled = true;
    
    /**
     * 排序
     */
    @Column(name = "order_num")
    private Integer order = 0;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
}
