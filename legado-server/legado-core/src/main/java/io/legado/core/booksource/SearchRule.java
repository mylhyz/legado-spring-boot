package io.legado.core.booksource;

import lombok.Data;

/**
 * 书源搜索规则
 */
@Data
public class SearchRule {
    
    /**
     * 书籍列表
     */
    private String bookList;
    
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
     * 详情页链接
     */
    private String bookUrl;
    
    /**
     * 最新章节
     */
    private String latestChapter;
    
    /**
     * 字数
     */
    private String wordCount;
    
    /**
     * 分类
     */
    private String kind;
    
}
