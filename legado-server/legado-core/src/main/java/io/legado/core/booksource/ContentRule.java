package io.legado.core.booksource;

import lombok.Data;

/**
 * 正文规则
 */
@Data
public class ContentRule {
    
    /**
     * 正文内容
     */
    private String content;
    
    /**
     * 下一页链接
     */
    private String nextContentUrl;
    
    /**
     * WebView加载
     */
    private String webJs;
    
    /**
     * 替换规则
     */
    private String replaceRegex;
    
    /**
     * 图片样式
     */
    private String imageStyle;
    
}
