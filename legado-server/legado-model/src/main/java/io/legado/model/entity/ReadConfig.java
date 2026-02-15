package io.legado.model.entity;

import lombok.Data;
import java.io.Serializable;

/**
 * 阅读配置
 */
@Data
public class ReadConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 是否倒序显示目录
     */
    private Boolean reverseToc = false;
    
    /**
     * 翻页动画
     */
    private Integer pageAnim;
    
    /**
     * 重新分段
     */
    private Boolean reSegment = false;
    
    /**
     * 图片样式
     */
    private String imageStyle;
    
    /**
     * 是否使用替换规则
     */
    private Boolean useReplaceRule;
    
    /**
     * 去除标签
     */
    private Long delTag = 0L;
    
    /**
     * TTS引擎
     */
    private String ttsEngine;
    
    /**
     * 是否分割长章节
     */
    private Boolean splitLongChapter = true;
    
    /**
     * 是否模拟阅读
     */
    private Boolean readSimulating = false;
    
    /**
     * 开始日期
     */
    private String startDate;
    
    /**
     * 开始章节
     */
    private Integer startChapter = 0;
    
    /**
     * 每日章节数
     */
    private Integer dailyChapters = 3;
    
}
