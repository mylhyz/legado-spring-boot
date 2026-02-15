package io.legado.model.repository;

import io.legado.model.entity.BookGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 书籍分组数据访问接口
 */
@Repository
public interface BookGroupRepository extends JpaRepository<BookGroup, Long> {
    
    /**
     * 根据是否显示查询
     */
    List<BookGroup> findByShowTrueOrderByOrderAsc();
    
    /**
     * 根据名称查询
     */
    BookGroup findByGroupName(String groupName);
    
    /**
     * 查询所有分组
     */
    List<BookGroup> findAllByOrderByOrderAsc();
    
}
