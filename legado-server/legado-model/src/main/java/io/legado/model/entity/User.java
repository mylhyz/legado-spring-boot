package io.legado.model.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@Entity
@Table(name = "users",
       indexes = {
           @Index(name = "idx_users_username", columnList = "username", unique = true),
           @Index(name = "idx_users_email", columnList = "email", unique = true)
       })
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户名
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    /**
     * 密码（加密存储）
     */
    @Column(name = "password", nullable = false, length = 256)
    private String password;
    
    /**
     * 邮箱
     */
    @Column(name = "email", unique = true, length = 100)
    private String email;
    
    /**
     * 昵称
     */
    @Column(name = "nickname", length = 50)
    private String nickname;
    
    /**
     * 头像URL
     */
    @Column(name = "avatar", length = 512)
    private String avatar;
    
    /**
     * 是否启用
     */
    @Column(name = "enabled")
    private Boolean enabled = true;
    
    /**
     * 角色
     */
    @Column(name = "roles", length = 256)
    private String roles = "USER";
    
    /**
     * 最后登录时间
     */
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
    
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
