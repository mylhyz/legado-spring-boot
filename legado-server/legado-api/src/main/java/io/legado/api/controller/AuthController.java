package io.legado.api.controller;

import io.legado.api.config.JwtTokenProvider;
import io.legado.api.dto.ApiResponse;
import io.legado.api.dto.LoginRequest;
import io.legado.api.dto.LoginResponse;
import io.legado.api.dto.RegisterRequest;
import io.legado.core.service.UserService;
import io.legado.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("用户注册: {}", request.getUsername());
        
        User user = userService.register(
                request.getUsername(),
                request.getPassword(),
                request.getEmail()
        );
        
        // 生成Token
        String token = tokenProvider.generateToken(user);
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(tokenProvider.getExpirationTime() / 1000);
        response.setUser(user);
        
        return ApiResponse.success(response);
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("用户登录: {}", request.getUsername());
        
        User user = userService.login(request.getUsername(), request.getPassword());
        
        // 生成Token
        String token = tokenProvider.generateToken(user);
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(tokenProvider.getExpirationTime() / 1000);
        response.setUser(user);
        
        return ApiResponse.success(response);
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ApiResponse<User> getCurrentUser(@RequestAttribute("user") User user) {
        return ApiResponse.success(user);
    }
    
}
