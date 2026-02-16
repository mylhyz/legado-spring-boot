package io.legado.core.booksource;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.legado.model.entity.Book;
import io.legado.model.entity.BookChapter;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 书源规则引擎
 * 使用GraalVM JavaScript引擎解析书源规则
 */
@Slf4j
@Component
public class BookSourceEngine {
    
    private static final ObjectMapper mapper = new ObjectMapper();
    private Context polyglot;
    
    @PostConstruct
    public void init() {
        try {
            polyglot = Context.newBuilder("js")
                    .allowAllAccess(true)
                    .build();
            log.info("GraalVM JavaScript引擎初始化成功");
        } catch (Exception e) {
            log.error("GraalVM JavaScript引擎初始化失败", e);
            throw new RuntimeException("无法初始化JavaScript引擎", e);
        }
    }
    
    @PreDestroy
    public void destroy() {
        if (polyglot != null) {
            polyglot.close();
            log.info("GraalVM JavaScript引擎已关闭");
        }
    }
    
    /**
     * 解析搜索结果
     */
    public List<Book> parseSearchResults(String html, String searchRuleJson, String sourceName, String sourceUrl) {
        List<Book> results = new ArrayList<>();
        
        try {
            SearchRule rule = mapper.readValue(searchRuleJson, SearchRule.class);
            Document doc = Jsoup.parse(html);
            
            // 获取书籍列表
            Elements bookList;
            if (rule.getBookList() != null && !rule.getBookList().isEmpty()) {
                bookList = selectElements(doc, rule.getBookList());
            } else {
                bookList = doc.select("body");
            }
            
            for (Element element : bookList) {
                try {
                    Book book = new Book();
                    book.setOrigin(sourceUrl);
                    book.setOriginName(sourceName);
                    
                    // 解析书名
                    if (rule.getName() != null) {
                        book.setName(parseText(element, rule.getName()));
                    }
                    
                    // 解析作者
                    if (rule.getAuthor() != null) {
                        book.setAuthor(parseText(element, rule.getAuthor()));
                    }
                    
                    // 解析简介
                    if (rule.getIntro() != null) {
                        book.setIntro(parseText(element, rule.getIntro()));
                    }
                    
                    // 解析封面
                    if (rule.getCoverUrl() != null) {
                        String coverUrl = parseAttr(element, rule.getCoverUrl(), "src");
                        book.setCoverUrl(absoluteUrl(coverUrl, sourceUrl));
                    }
                    
                    // 解析书籍URL
                    if (rule.getBookUrl() != null) {
                        String bookUrl = parseAttr(element, rule.getBookUrl(), "href");
                        book.setBookUrl(absoluteUrl(bookUrl, sourceUrl));
                    }
                    
                    // 解析最新章节
                    if (rule.getLatestChapter() != null) {
                        book.setLatestChapterTitle(parseText(element, rule.getLatestChapter()));
                    }
                    
                    // 解析字数
                    if (rule.getWordCount() != null) {
                        book.setWordCount(parseText(element, rule.getWordCount()));
                    }
                    
                    // 解析分类
                    if (rule.getKind() != null) {
                        book.setKind(parseText(element, rule.getKind()));
                    }
                    
                    // 验证必要字段
                    if (book.getName() != null && !book.getName().isEmpty() 
                            && book.getBookUrl() != null && !book.getBookUrl().isEmpty()) {
                        results.add(book);
                    }
                    
                } catch (Exception e) {
                    log.warn("解析单个书籍失败", e);
                }
            }
            
        } catch (Exception e) {
            log.error("解析搜索结果失败", e);
        }
        
        return results;
    }
    
    /**
     * 解析书籍详情
     */
    public Book parseBookInfo(String html, String bookInfoRuleJson, Book book) {
        try {
            BookInfoRule rule = mapper.readValue(bookInfoRuleJson, BookInfoRule.class);
            Document doc = Jsoup.parse(html);
            
            // 解析书名
            if (rule.getName() != null) {
                String name = parseText(doc, rule.getName());
                if (name != null && !name.isEmpty()) {
                    book.setName(name);
                }
            }
            
            // 解析作者
            if (rule.getAuthor() != null) {
                book.setAuthor(parseText(doc, rule.getAuthor()));
            }
            
            // 解析简介
            if (rule.getIntro() != null) {
                book.setIntro(parseText(doc, rule.getIntro()));
            }
            
            // 解析封面
            if (rule.getCoverUrl() != null) {
                String coverUrl = parseAttr(doc, rule.getCoverUrl(), "src");
                book.setCoverUrl(absoluteUrl(coverUrl, book.getOrigin()));
            }
            
            // 解析分类
            if (rule.getKind() != null) {
                book.setKind(parseText(doc, rule.getKind()));
            }
            
            // 解析最新章节
            if (rule.getLatestChapter() != null) {
                book.setLatestChapterTitle(parseText(doc, rule.getLatestChapter()));
            }
            
            // 解析最新章节时间
            if (rule.getLatestChapterTime() != null) {
                // TODO: 解析时间
            }
            
            // 解析目录链接
            if (rule.getTocUrl() != null) {
                String tocUrl = parseAttr(doc, rule.getTocUrl(), "href");
                book.setTocUrl(absoluteUrl(tocUrl, book.getOrigin()));
            }
            
            // 解析字数
            if (rule.getWordCount() != null) {
                book.setWordCount(parseText(doc, rule.getWordCount()));
            }
            
        } catch (Exception e) {
            log.error("解析书籍详情失败", e);
        }
        
        return book;
    }
    
    /**
     * 解析章节目录
     */
    public List<BookChapter> parseToc(String html, String tocRuleJson, Long bookId, String baseUrl) {
        List<BookChapter> chapters = new ArrayList<>();
        
        try {
            TocRule rule = mapper.readValue(tocRuleJson, TocRule.class);
            Document doc = Jsoup.parse(html);
            
            // 获取章节列表
            Elements chapterList = selectElements(doc, rule.getChapterList());
            
            int index = 0;
            for (Element element : chapterList) {
                try {
                    BookChapter chapter = new BookChapter();
                    chapter.setBookId(bookId);
                    chapter.setChapterIndex(index);
                    
                    // 解析章节名
                    if (rule.getChapterName() != null) {
                        chapter.setTitle(parseText(element, rule.getChapterName()));
                    }
                    
                    // 解析章节链接
                    if (rule.getChapterUrl() != null) {
                        String chapterUrl = parseAttr(element, rule.getChapterUrl(), "href");
                        chapter.setUrl(absoluteUrl(chapterUrl, baseUrl));
                    }
                    
                    if (chapter.getTitle() != null && !chapter.getTitle().isEmpty()) {
                        chapters.add(chapter);
                        index++;
                    }
                    
                } catch (Exception e) {
                    log.warn("解析单个章节失败", e);
                }
            }
            
            // 如果需要倒序
            if (rule.getIsReverse() != null && rule.getIsReverse()) {
                // 重新设置索引
                for (int i = 0; i < chapters.size(); i++) {
                    chapters.get(i).setChapterIndex(i);
                }
            }
            
        } catch (Exception e) {
            log.error("解析章节目录失败", e);
        }
        
        return chapters;
    }
    
    /**
     * 解析正文内容
     */
    public String parseContent(String html, String contentRuleJson) {
        StringBuilder content = new StringBuilder();
        
        try {
            ContentRule rule = mapper.readValue(contentRuleJson, ContentRule.class);
            Document doc = Jsoup.parse(html);
            
            // 获取正文内容
            if (rule.getContent() != null) {
                Elements elements = selectElements(doc, rule.getContent());
                for (Element element : elements) {
                    content.append(element.html());
                }
            }
            
        } catch (Exception e) {
            log.error("解析正文内容失败", e);
        }
        
        return content.toString();
    }
    
    /**
     * 选择元素
     */
    private Elements selectElements(Element element, String rule) {
        if (rule == null || rule.isEmpty()) {
            return new Elements();
        }
        
        // 处理CSS选择器前缀 @css:
        if (rule.startsWith("@css:")) {
            rule = rule.substring(5);
        }
        
        // 处理JSON路径
        if (rule.startsWith("$.")) {
            // TODO: 实现JSON路径解析
            return new Elements();
        }
        
        return element.select(rule);
    }
    
    /**
     * 解析文本
     */
    private String parseText(Element element, String rule) {
        if (rule == null || rule.isEmpty()) {
            return "";
        }
        
        // 处理CSS选择器前缀 @css:
        if (rule.startsWith("@css:")) {
            rule = rule.substring(5);
        }
        
        // 处理正则表达式
        if (rule.contains("&&")) {
            String[] parts = rule.split("&&");
            String selector = parts[0].trim();
            String regex = parts[1].trim().replace("##", "");
            
            Elements elements = element.select(selector);
            if (!elements.isEmpty()) {
                String text = elements.first().text();
                // 应用正则
                if (!regex.isEmpty()) {
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(text);
                    if (matcher.find()) {
                        return matcher.group(1);
                    }
                }
                return text;
            }
        }
        
        // 普通CSS选择器
        Elements elements = element.select(rule);
        if (!elements.isEmpty()) {
            return elements.first().text();
        }
        
        return "";
    }
    
    /**
     * 解析属性
     */
    private String parseAttr(Element element, String rule, String attr) {
        if (rule == null || rule.isEmpty()) {
            return "";
        }
        
        // 处理CSS选择器前缀 @css:
        if (rule.startsWith("@css:")) {
            rule = rule.substring(5);
        }
        
        // 检查是否指定了属性
        if (rule.contains("@")) {
            String[] parts = rule.split("@");
            rule = parts[0].trim();
            attr = parts[1].trim();
        }
        
        Elements elements = element.select(rule);
        if (!elements.isEmpty()) {
            return elements.first().attr(attr);
        }
        
        return "";
    }
    
    /**
     * 转换为绝对URL
     */
    private String absoluteUrl(String url, String baseUrl) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        
        if (baseUrl == null || baseUrl.isEmpty()) {
            return url;
        }
        
        // 处理相对路径
        try {
            java.net.URL base = new java.net.URL(baseUrl);
            java.net.URL absolute = new java.net.URL(base, url);
            return absolute.toString();
        } catch (Exception e) {
            return url;
        }
    }
    
}
