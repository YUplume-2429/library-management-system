package com.library.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书DTO（用于前端交互）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {
    
    /**
     * 图书ID
     */
    private Integer bookId;
    
    /**
     * ISBN
     */
    private String isbn;
    
    /**
     * 书名
     */
    private String title;
    
    /**
     * 作者
     */
    private String author;
    
    /**
     * 出版社
     */
    private String publisher;
    
    /**
     * 出版日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishDate;
    
    /**
     * 分类
     */
    private String category;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 库存
     */
    private Integer stock;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 是否可借阅
     */
    private Boolean isAvailable;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
