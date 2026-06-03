package com.library.service;

import com.library.dto.BorrowRecordDTO;

import java.util.List;

/**
 * 借阅服务接口
 */
public interface BorrowService {

    /**
     * 借阅图书
     */
    BorrowRecordDTO borrowBook(Integer bookId, Integer userId);

    /**
     * 归还图书
     */
    BorrowRecordDTO returnBook(Integer recordId, Integer userId, boolean admin);

    /**
     * 获取当前用户借阅记录
     */
    List<BorrowRecordDTO> getUserRecords(Integer userId);

    /**
     * 管理员获取全部借阅记录
     */
    List<BorrowRecordDTO> getAllRecords();
}
