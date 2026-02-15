package io.legado.model.repository;

import io.legado.model.entity.BookChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 书籍章节数据访问接口
 */
@Repository
public interface BookChapterRepository extends JpaRepository<BookChapter, Long> {
    
    /**
     * 根据书籍ID查询所有章节
     */
    List<BookChapter> findByBookIdOrderByChapterIndexAsc(Long bookId);
    
    /**
     * 根据书籍ID和章节索引查询
     */
    Optional<BookChapter> findByBookIdAndChapterIndex(Long bookId, Integer chapterIndex);
    
    /**
     * 根据书籍ID查询章节数量
     */
    long countByBookId(Long bookId);
    
    /**
     * 删除书籍的所有章节
     */
    @Modifying
    @Query("DELETE FROM BookChapter c WHERE c.bookId = :bookId")
    void deleteByBookId(@Param("bookId") Long bookId);
    
    /**
     * 根据书籍ID查询有内容的章节数量
     */
    @Query("SELECT COUNT(c) FROM BookChapter c WHERE c.bookId = :bookId AND c.content IS NOT NULL AND c.content != ''")
    long countCachedChaptersByBookId(@Param("bookId") Long bookId);
    
    /**
     * 查询书籍的最后一个章节索引
     */
    @Query("SELECT MAX(c.chapterIndex) FROM BookChapter c WHERE c.bookId = :bookId")
    Integer findMaxChapterIndexByBookId(@Param("bookId") Long bookId);
    
}
