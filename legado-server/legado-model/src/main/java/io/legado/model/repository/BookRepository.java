package io.legado.model.repository;

import io.legado.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 书籍数据访问接口
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    /**
     * 根据书籍URL查询
     */
    Optional<Book> findByBookUrl(String bookUrl);
    
    /**
     * 检查书籍URL是否存在
     */
    boolean existsByBookUrl(String bookUrl);
    
    /**
     * 根据书名和作者查询
     */
    Optional<Book> findByNameAndAuthor(String name, String author);
    
    /**
     * 根据书名模糊查询
     */
    List<Book> findByNameContainingIgnoreCase(String name);
    
    /**
     * 根据分组ID查询
     */
    List<Book> findByGroupId(Long groupId);
    
    /**
     * 根据书源查询
     */
    List<Book> findByOrigin(String origin);
    
    /**
     * 查询可更新的书籍
     */
    List<Book> findByCanUpdateTrue();
    
    /**
     * 分页查询所有书籍
     */
    Page<Book> findAllByOrderByUpdatedAtDesc(Pageable pageable);
    
    /**
     * 搜索书籍
     */
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Book> searchBooks(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 统计书籍数量
     */
    long countByGroupId(Long groupId);
    
}
