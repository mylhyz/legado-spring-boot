package io.legado.api.config;

import io.legado.model.entity.User;
import io.legado.model.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 从请求中获取JWT Token
            String jwt = getJwtFromRequest(request);
            
            // 验证Token
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // 获取用户ID
                Long userId = tokenProvider.getUserIdFromToken(jwt);
                
                // 查询用户
                User user = userRepository.findById(userId).orElse(null);
                
                if (user != null && user.getEnabled()) {
                    // 解析角色
                    List<SimpleGrantedAuthority> authorities = Arrays.stream(user.getRoles().split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                    
                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 设置安全上下文
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            log.error("无法设置用户认证", e);
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 从请求中获取JWT Token
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
}
