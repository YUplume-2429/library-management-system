package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("book")
@Entity
@Table(name = "book")
public class Book {
    
    /**
     * 图书ID
     */
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Integer bookId;
    
    /**
     * 国际标准书号（ISBN）
     */
    @Column(name = "isbn", unique = true, nullable = false)
    private String isbn;
    
    /**
     * 书名
     */
    @Column(name = "title", nullable = false)
    private String title;
    
    /**
     * 作者
     */
    @Column(name = "author", nullable = false)
    private String author;
    
    /**
     * 出版社
     */
    @Column(name = "publisher", nullable = false)
    private String publisher;
    
    /**
     * 出版日期
     */
    @Column(name = "publish_date")
    private LocalDate publishDate;
    
    /**
     * 分类
     */
    @Column(name = "category")
    private String category;
    
    /**
     * 价格
     */
    @Column(name = "price")
    private BigDecimal price;
    
    /**
     * 库存数量（必须>= 0）
     */
    @Column(name = "stock", nullable = false)
    private Integer stock;
    
    /**
     * 图书描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /**
     * 是否可借阅
     */
    @Column(name = "is_available")
    private Boolean isAvailable;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
