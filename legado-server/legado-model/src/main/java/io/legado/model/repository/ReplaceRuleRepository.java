package io.legado.model.repository;

import io.legado.model.entity.ReplaceRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 替换规则数据访问接口
 */
@Repository
public interface ReplaceRuleRepository extends JpaRepository<ReplaceRule, Long> {
    
    /**
     * 查询所有启用的规则
     */
    List<ReplaceRule> findByIsEnabledTrueOrderByOrderAsc();
    
    /**
     * 根据名称查询
     */
    List<ReplaceRule> findByNameContainingIgnoreCase(String name);
    
    /**
     * 根据作用范围查询
     */
    List<ReplaceRule> findByScopeContainingIgnoreCase(String scope);
    
}
