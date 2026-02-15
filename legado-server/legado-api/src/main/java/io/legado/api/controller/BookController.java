package io.legado.api.controller;

import io.legado.api.dto.ApiResponse;
import io.legado.core.dto.SearchResultDto;
import io.legado.core.service.BookSearchService;
import io.legado.core.service.BookService;
import io.legado.model.entity.Book;
import io.legado.model.entity.BookChapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 书籍API控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private BookSearchService bookSearchService;
    
    /**
     * 获取所有书籍
     */
    @GetMapping
    public ApiResponse<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ApiResponse.success(books);
    }
    
    /**
     * 获取书籍详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Book> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            return ApiResponse.error(404, "书籍不存在");
        }
        return ApiResponse.success(book);
    }
    
    /**
     * 添加书籍
     */
    @PostMapping
    public ApiResponse<Book> addBook(@RequestBody Book book) {
        Book saved = bookService.addBook(book);
        return ApiResponse.success(saved);
    }
    
    /**
     * 从书源添加书籍
     */
    @PostMapping("/from-source")
    public ApiResponse<Book> addBookFromSource(
            @RequestParam String bookUrl,
            @RequestParam String sourceUrl) {
        Book book = bookService.addBookFromSource(bookUrl, sourceUrl);
        return ApiResponse.success(book);
    }
    
    /**
     * 删除书籍
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ApiResponse.success(null);
    }
    
    /**
     * 获取书籍章节列表
     */
    @GetMapping("/{id}/chapters")
    public ApiResponse<List<BookChapter>> getChapters(@PathVariable Long id) {
        List<BookChapter> chapters = bookService.getChapters(id);
        return ApiResponse.success(chapters);
    }
    
    /**
     * 获取章节内容
     */
    @GetMapping("/{id}/chapters/{index}/content")
    public ApiResponse<String> getChapterContent(
            @PathVariable Long id,
            @PathVariable Integer index) {
        String content = bookService.getChapterContent(id, index);
        return ApiResponse.success(content);
    }
    
    /**
     * 更新阅读进度
     */
    @PutMapping("/{id}/progress")
    public ApiResponse<Void> updateProgress(
            @PathVariable Long id,
            @RequestParam Integer chapterIndex,
            @RequestParam(required = false, defaultValue = "0") Integer chapterPos) {
        bookService.updateProgress(id, chapterIndex, chapterPos);
        return ApiResponse.success(null);
    }
    
    /**
     * 搜索书籍
     */
    @GetMapping("/search")
    public ApiResponse<List<SearchResultDto>> searchBooks(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ApiResponse.error(400, "搜索关键词不能为空");
        }
        
        List<SearchResultDto> results = bookSearchService.search(keyword);
        return ApiResponse.success(results);
    }
    
}
