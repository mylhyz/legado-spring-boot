package io.legado.core.booksource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 书籍信息规则
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookInfoRule {
    
    /**
     * 书名
     */
    private String name;
    
    /**
     * 作者
     */
    private String author;
    
    /**
     * 简介
     */
    private String intro;
    
    /**
     * 封面
     */
    private String coverUrl;
    
    /**
     * 分类
     */
    private String kind;
    
    /**
     * 最新章节
     */
    private String latestChapter;
    
    /**
     * 最新章节时间
     */
    private String latestChapterTime;
    
    /**
     * 目录链接
     */
    private String tocUrl;
    
    /**
     * 字数
     */
    private String wordCount;
    
}
