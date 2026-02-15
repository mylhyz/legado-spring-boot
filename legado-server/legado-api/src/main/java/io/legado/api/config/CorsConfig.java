package io.legado.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * CORS跨域配置
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Value("${legado.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;
    
    @Value("${legado.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;
    
    @Value("${legado.cors.allowed-headers:*}")
    private String allowedHeaders;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = allowedOrigins.split(",");
        
        registry.addMapping("/api/**")
                .allowedOrigins(origins)
                .allowedMethods(allowedMethods.split(","))
                .allowedHeaders(allowedHeaders)
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        
        String[] origins = allowedOrigins.split(",");
        config.setAllowedOrigins(Arrays.asList(origins));
        
        config.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
        config.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
    
}
