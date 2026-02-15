package io.legado.model.entity;

import io.legado.model.converter.ReadConfigConverter;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 书籍实体
 */
@Data
@Entity
@Table(name = "books", 
       indexes = {
           @Index(name = "idx_books_name_author", columnList = "name, author"),
           @Index(name = "idx_books_origin", columnList = "origin"),
           @Index(name = "idx_books_group_id", columnList = "groupId")
       })
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 书籍URL（主键）
     */
    @Column(name = "book_url", nullable = false, unique = true, length = 2048)
    private String bookUrl;
    
    /**
     * 目录URL
     */
    @Column(name = "toc_url", length = 2048)
    private String tocUrl;
    
    /**
     * 书源URL
     */
    @Column(name = "origin", nullable = false, length = 256)
    private String origin = "local";
    
    /**
     * 书源名称
     */
    @Column(name = "origin_name", length = 256)
    private String originName;
    
    /**
     * 书名
     */
    @Column(name = "name", nullable = false, length = 256)
    private String name;
    
    /**
     * 作者
     */
    @Column(name = "author", length = 256)
    private String author;
    
    /**
     * 分类
     */
    @Column(name = "kind", length = 512)
    private String kind;
    
    /**
     * 自定义标签
     */
    @Column(name = "custom_tag", length = 512)
    private String customTag;
    
    /**
     * 封面URL
     */
    @Column(name = "cover_url", length = 2048)
    private String coverUrl;
    
    /**
     * 自定义封面URL
     */
    @Column(name = "custom_cover_url", length = 2048)
    private String customCoverUrl;
    
    /**
     * 简介
     */
    @Column(name = "intro", columnDefinition = "TEXT")
    private String intro;
    
    /**
     * 自定义简介
     */
    @Column(name = "custom_intro", columnDefinition = "TEXT")
    private String customIntro;
    
    /**
     * 字符集
     */
    @Column(name = "charset", length = 32)
    private String charset;
    
    /**
     * 类型
     */
    @Column(name = "type")
    private Integer type = 0;
    
    /**
     * 分组ID
     */
    @Column(name = "group_id")
    private Long groupId = 0L;
    
    /**
     * 最新章节标题
     */
    @Column(name = "latest_chapter_title", length = 512)
    private String latestChapterTitle;
    
    /**
     * 最新章节时间
     */
    @Column(name = "latest_chapter_time")
    private Long latestChapterTime;
    
    /**
     * 最后检查时间
     */
    @Column(name = "last_check_time")
    private Long lastCheckTime;
    
    /**
     * 最后检查数量
     */
    @Column(name = "last_check_count")
    private Integer lastCheckCount = 0;
    
    /**
     * 总章节数
     */
    @Column(name = "total_chapter_num")
    private Integer totalChapterNum = 0;
    
    /**
     * 当前章节标题
     */
    @Column(name = "dur_chapter_title", length = 512)
    private String durChapterTitle;
    
    /**
     * 当前章节索引
     */
    @Column(name = "dur_chapter_index")
    private Integer durChapterIndex = 0;
    
    /**
     * 当前阅读位置
     */
    @Column(name = "dur_chapter_pos")
    private Integer durChapterPos = 0;
    
    /**
     * 当前章节时间
     */
    @Column(name = "dur_chapter_time")
    private Long durChapterTime;
    
    /**
     * 字数
     */
    @Column(name = "word_count", length = 64)
    private String wordCount;
    
    /**
     * 是否可以更新
     */
    @Column(name = "can_update")
    private Boolean canUpdate = true;
    
    /**
     * 排序
     */
    @Column(name = "order_num")
    private Integer order = 0;
    
    /**
     * 书源排序
     */
    @Column(name = "origin_order")
    private Integer originOrder = 0;
    
    /**
     * 变量
     */
    @Column(name = "variable", columnDefinition = "TEXT")
    private String variable;
    
    /**
     * 阅读配置
     */
    @Column(name = "read_config", columnDefinition = "TEXT")
    @Convert(converter = ReadConfigConverter.class)
    private ReadConfig readConfig;
    
    /**
     * 同步时间
     */
    @Column(name = "sync_time")
    private Long syncTime;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 章节列表
     */
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookChapter> chapters;
    
    /**
     * 获取实际作者名（去除多余字符）
     */
    public String getRealAuthor() {
        if (author == null) {
            return "";
        }
        return author.replaceAll("[：:\\s]", "").trim();
    }
    
    /**
     * 获取显示用的封面URL
     */
    public String getDisplayCoverUrl() {
        if (customCoverUrl != null && !customCoverUrl.isEmpty()) {
            return customCoverUrl;
        }
        return coverUrl;
    }
    
    /**
     * 获取显示用的简介
     */
    public String getDisplayIntro() {
        if (customIntro != null && !customIntro.isEmpty()) {
            return customIntro;
        }
        return intro;
    }
    
    /**
     * 获取未读章节数
     */
    public Integer getUnreadChapterNum() {
        int simulatedTotal = totalChapterNum;
        if (simulatedTotal <= 0) {
            simulatedTotal = 1;
        }
        return Math.max(simulatedTotal - durChapterIndex - 1, 0);
    }
    
}
