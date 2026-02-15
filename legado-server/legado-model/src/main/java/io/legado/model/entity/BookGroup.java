package io.legado.model.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 书籍分组实体
 */
@Data
@Entity
@Table(name = "book_groups")
public class BookGroup {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 分组名称
     */
    @Column(name = "group_name", nullable = false, length = 256)
    private String groupName;
    
    /**
     * 分组图标
     */
    @Column(name = "group_icon", length = 2048)
    private String groupIcon;
    
    /**
     * 排序
     */
    @Column(name = "order_num")
    private Integer order = 0;
    
    /**
     * 是否显示
     */
    @Column(name = "show")
    private Boolean show = true;
    
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
