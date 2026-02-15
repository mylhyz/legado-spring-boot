package io.legado.model.repository;

import io.legado.model.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 书签数据访问接口
 */
@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    
    /**
     * 根据书名和作者查询书签
     */
    List<Bookmark> findByBookNameAndBookAuthorOrderByCreatedAtDesc(String bookName, String bookAuthor);
    
    /**
     * 根据书籍URL查询书签
     */
    List<Bookmark> findByBookUrlOrderByCreatedAtDesc(String bookUrl);
    
    /**
     * 分页查询所有书签
     */
    Page<Bookmark> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
}
