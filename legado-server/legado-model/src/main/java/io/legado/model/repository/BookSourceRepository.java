package io.legado.model.repository;

import io.legado.model.entity.BookSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 书源数据访问接口
 */
@Repository
public interface BookSourceRepository extends JpaRepository<BookSource, Long> {
    
    /**
     * 根据书源URL查询
     */
    Optional<BookSource> findBySourceUrl(String sourceUrl);
    
    /**
     * 查询所有启用的书源
     */
    List<BookSource> findByEnabledTrueOrderByWeightDesc();
    
    /**
     * 根据分组查询书源
     */
    List<BookSource> findBySourceGroupOrderByWeightDesc(String sourceGroup);
    
    /**
     * 根据名称模糊查询
     */
    List<BookSource> findBySourceNameContainingIgnoreCase(String sourceName);
    
    /**
     * 分页查询书源
     */
    Page<BookSource> findAllByOrderByWeightDesc(Pageable pageable);
    
    /**
     * 搜索书源
     */
    @Query("SELECT s FROM BookSource s WHERE " +
           "LOWER(s.sourceName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.sourceUrl) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<BookSource> searchSources(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 统计启用的书源数量
     */
    long countByEnabledTrue();
    
    /**
     * 检查书源URL是否存在
     */
    boolean existsBySourceUrl(String sourceUrl);
    
}
