package com.library.service;

import com.library.dto.BookDTO;
import java.util.List;

/**
 * 图书服务接口
 */
public interface BookService {
    
    /**
     * 获取所有图书
     */
    List<BookDTO> getAllBooks();
    
    /**
     * 根据图书ID获取图书
     */
    BookDTO getBookById(Integer bookId);
    
    /**
     * 模糊搜索图书（按书名）
     */
    List<BookDTO> searchByTitle(String keyword);
    
    /**
     * 创建图书
     */
    BookDTO createBook(BookDTO bookDTO);

    /**
     * 批量导入图书
     */
    List<BookDTO> importBooks(List<BookDTO> bookDTOList);
    
    /**
     * 更新图书
     */
    BookDTO updateBook(BookDTO bookDTO);
    
    /**
     * 删除图书
     */
    boolean deleteBook(Integer bookId);
    
    /**
     * 获取所有可用的图书
     */
    List<BookDTO> getAvailableBooks();

    /**
     * 按分类查询图书
     */
    List<BookDTO> getBooksByCategory(String category);

    /**
     * 获取全部图书分类
     */
    List<String> getCategories();
}
