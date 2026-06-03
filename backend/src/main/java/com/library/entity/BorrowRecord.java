package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 借阅记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("borrow_record")
@Entity
@Table(name = "borrow_record")
public class BorrowRecord {
    
    /**
     * 借阅记录ID
     */
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Integer recordId;
    
    /**
     * 用户ID（外键）
     */
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    
    /**
     * 图书ID（外键）
     */
    @Column(name = "book_id", nullable = false)
    private Integer bookId;
    
    /**
     * 借阅时间
     */
    @Column(name = "borrow_date", nullable = false)
    private LocalDateTime borrowDate;
    
    /**
     * 应还期限
     */
    @Column(name = "due_date")
    private LocalDate dueDate;
    
    /**
     * 实际归还时间
     */
    @Column(name = "return_date")
    private LocalDateTime returnDate;
    
    /**
     * 状态：BORROWING-借阅中，RETURNED-已归还
     */
    @Column(name = "status")
    private String status;
    
    /**
     * 备注
     */
    @Column(name = "notes")
    private String notes;
    
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
