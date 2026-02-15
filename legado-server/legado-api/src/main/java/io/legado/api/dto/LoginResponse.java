package io.legado.api.dto;

import io.legado.model.entity.User;
import lombok.Data;

/**
 * 登录响应
 */
@Data
public class LoginResponse {
    
    /**
     * JWT Token
     */
    private String token;
    
    /**
     * Token类型
     */
    private String tokenType;
    
    /**
     * 过期时间（秒）
     */
    private Long expiresIn;
    
    /**
     * 用户信息
     */
    private User user;
    
}
