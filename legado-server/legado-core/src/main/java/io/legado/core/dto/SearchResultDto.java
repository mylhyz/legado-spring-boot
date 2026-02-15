package io.legado.core.dto;

import lombok.Data;

/**
 * 搜索结果DTO
 */
@Data
public class SearchResultDto {
    
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
     * 封面URL
     */
    private String coverUrl;
    
    /**
     * 书籍URL
     */
    private String bookUrl;
    
    /**
     * 最新章节标题
     */
    private String latestChapterTitle;
    
    /**
     * 字数
     */
    private String wordCount;
    
    /**
     * 分类
     */
    private String kind;
    
    /**
     * 书源名称
     */
    private String sourceName;
    
    /**
     * 书源URL
     */
    private String sourceUrl;
    
}
