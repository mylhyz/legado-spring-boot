package io.legado.core.booksource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 目录规则
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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
