package io.legado.core.booksource;

import lombok.Data;

/**
 * 目录规则
 */
@Data
public class TocRule {
    
    /**
     * 章节列表
     */
    private String chapterList;
    
    /**
     * 章节名称
     */
    private String chapterName;
    
    /**
     * 章节链接
     */
    private String chapterUrl;
    
    /**
     * 下一页链接
     */
    private String nextTocUrl;
    
    /**
     * 是否倒序
     */
    private Boolean isReverse;
    
}
