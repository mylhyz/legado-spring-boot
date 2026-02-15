package io.legado.model.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 书签实体
 */
@Data
@Entity
@Table(name = "bookmarks",
       indexes = {
           @Index(name = "idx_bookmarks_book", columnList = "book_name, book_author")
       })
public class Bookmark {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 书名
     */
    @Column(name = "book_name", nullable = false, length = 256)
    private String bookName;
    
    /**
     * 作者
     */
    @Column(name = "book_author", length = 256)
    private String bookAuthor;
    
    /**
     * 书籍URL
     */
    @Column(name = "book_url", length = 2048)
    private String bookUrl;
    
    /**
     * 章节名
     */
    @Column(name = "chapter_name", length = 512)
    private String chapterName;
    
    /**
     * 章节索引
     */
    @Column(name = "chapter_index")
    private Integer chapterIndex;
    
    /**
     * 页面位置
     */
    @Column(name = "page_index")
    private Integer pageIndex;
    
    /**
     * 内容
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    /**
     * 备注
     */
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
}
