package io.legado.core.service;

import io.legado.model.entity.User;
import io.legado.model.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务
 */
@Slf4j
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 用户注册
     */
    @Transactional
    public User register(String username, String password, String email) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (email != null && userRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setNickname(username);
        user.setEnabled(true);
        user.setRoles("USER");
        
        return userRepository.save(user);
    }
    
    /**
     * 用户登录验证
     */
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (!user.getEnabled()) {
            throw new RuntimeException("用户已被禁用");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
        
        return user;
    }
    
    /**
     * 根据ID获取用户
     */
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    /**
     * 根据用户名获取用户
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    /**
     * 更新用户信息
     */
    @Transactional
    public User updateUser(Long id, String nickname, String email, String avatar) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (nickname != null) {
            user.setNickname(nickname);
        }
        
        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new RuntimeException("邮箱已被使用");
            }
            user.setEmail(email);
        }
        
        if (avatar != null) {
            user.setAvatar(avatar);
        }
        
        return userRepository.save(user);
    }
    
    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
}
