package io.legado.model.repository;

import io.legado.model.entity.Cookie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Cookie数据访问接口
 */
@Repository
public interface CookieRepository extends JpaRepository<Cookie, Long> {
    
    /**
     * 根据URL查询Cookie
     */
    Optional<Cookie> findByUrl(String url);
    
    /**
     * 删除指定URL的Cookie
     */
    void deleteByUrl(String url);
    
}
