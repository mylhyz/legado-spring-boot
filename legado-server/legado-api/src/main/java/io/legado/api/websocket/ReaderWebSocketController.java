package io.legado.api.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

/**
 * 阅读器WebSocket控制器
 */
@Slf4j
@Controller
public class ReaderWebSocketController {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * 接收阅读进度更新
     */
    @MessageMapping("/reader/progress")
    public void updateProgress(@Payload Map<String, Object> payload, Principal principal) {
        String username = principal != null ? principal.getName() : "anonymous";
        log.info("用户 {} 更新阅读进度: {}", username, payload);
        
        // 可以在这里处理进度保存逻辑
        Long bookId = Long.valueOf(payload.get("bookId").toString());
        Integer chapterIndex = Integer.valueOf(payload.get("chapterIndex").toString());
        Integer chapterPos = Integer.valueOf(payload.get("chapterPos").toString());
        
        // 广播给其他设备（多端同步）
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/progress-sync",
                payload
        );
    }
    
    /**
     * 接收章节缓存进度
     */
    @MessageMapping("/reader/cache-progress")
    public void cacheProgress(@Payload Map<String, Object> payload, Principal principal) {
        String username = principal != null ? principal.getName() : "anonymous";
        
        // 发送缓存进度到用户
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/cache-progress",
                payload
        );
    }
    
    /**
     * 广播缓存进度（供服务层调用）
     */
    public void broadcastCacheProgress(String username, Long bookId, int cachedCount, int totalCount) {
        Map<String, Object> progress = Map.of(
                "bookId", bookId,
                "cachedCount", cachedCount,
                "totalCount", totalCount,
                "progress", totalCount > 0 ? (cachedCount * 100 / totalCount) : 0
        );
        
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/cache-progress",
                progress
        );
    }
    
}
