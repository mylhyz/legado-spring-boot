package io.legado.model.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 书籍章节实体
 */
@Data
@Entity
@Table(name = "book_chapters",
       indexes = {
           @Index(name = "idx_chapters_book_id", columnList = "book_id")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_book_chapter", columnNames = {"book_id", "chapter_index"})
       })
public class BookChapter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 书籍ID
     */
    @Column(name = "book_id", nullable = false)
    private Long bookId;
    
    /**
     * 章节索引
     */
    @Column(name = "chapter_index", nullable = false)
    private Integer chapterIndex;
    
    /**
     * 章节标题
     */
    @Column(name = "title", length = 512)
    private String title;
    
    /**
     * 章节URL
     */
    @Column(name = "url", length = 2048)
    private String url;
    
    /**
     * 章节内容
     */
    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;
    
    /**
     * 字数
     */
    @Column(name = "word_count")
    private Integer wordCount;
    
    /**
     * 是否VIP
     */
    @Column(name = "is_vip")
    private Boolean isVip = false;
    
    /**
     * 是否付费
     */
    @Column(name = "is_pay")
    private Boolean isPay = false;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * 所属书籍
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", insertable = false, updatable = false)
    private Book book;
    
    /**
     * 获取显示标题
     */
    public String getDisplayTitle() {
        if (title == null || title.isEmpty()) {
            return "第" + (chapterIndex + 1) + "章";
        }
        return title;
    }
    
}
