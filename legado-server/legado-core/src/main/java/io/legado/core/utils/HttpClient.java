package io.legado.core.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * HTTP客户端工具
 */
@Slf4j
@Component
public class HttpClient {
    
    private OkHttpClient client;
    
    @PostConstruct
    public void init() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .build();
    }
    
    /**
     * GET请求
     */
    public String get(String url) throws IOException {
        return get(url, null, null);
    }
    
    /**
     * GET请求（带请求头）
     */
    public String get(String url, Map<String, String> headers) throws IOException {
        return get(url, headers, null);
    }
    
    /**
     * GET请求（带请求头和Cookie）
     */
    public String get(String url, Map<String, String> headers, String cookie) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);
        
        // 添加默认User-Agent
        builder.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        
        // 添加自定义请求头
        if (headers != null) {
            headers.forEach(builder::header);
        }
        
        // 添加Cookie
        if (cookie != null && !cookie.isEmpty()) {
            builder.header("Cookie", cookie);
        }
        
        Request request = builder.build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }
            
            ResponseBody body = response.body();
            return body != null ? body.string() : "";
        }
    }
    
    /**
     * POST请求
     */
    public String post(String url, Map<String, String> headers, String body, String cookie) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);
        
        // 添加默认User-Agent
        builder.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        
        // 添加自定义请求头
        if (headers != null) {
            headers.forEach(builder::header);
        }
        
        // 添加Cookie
        if (cookie != null && !cookie.isEmpty()) {
            builder.header("Cookie", cookie);
        }
        
        // 添加请求体
        RequestBody requestBody = RequestBody.create(
                body != null ? body : "",
                MediaType.parse("application/x-www-form-urlencoded; charset=utf-8")
        );
        builder.post(requestBody);
        
        Request request = builder.build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }
            
            ResponseBody responseBody = response.body();
            return responseBody != null ? responseBody.string() : "";
        }
    }
    
}
