package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.library.dto.BorrowRecordDTO;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.User;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.mapper.UserMapper;
import com.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private static final String BORROWING = "BORROWING";
    private static final String RETURNED = "RETURNED";
    private static final int DEFAULT_BORROW_DAYS = 30;

    private final BorrowRecordMapper borrowRecordMapper;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public BorrowRecordDTO borrowBook(Integer bookId, Integer userId) {
        if (bookId == null || userId == null) {
            throw new IllegalArgumentException("Book ID and user ID cannot be empty");
        }

        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book does not exist");
        }
        if (!Boolean.TRUE.equals(book.getIsAvailable()) || book.getStock() == null || book.getStock() <= 0) {
            throw new IllegalArgumentException("Book is not available for borrowing");
        }
        if (hasActiveBorrowRecord(userId, bookId)) {
            throw new IllegalArgumentException("You already have an active borrow record for this book");
        }

        LocalDateTime now = LocalDateTime.now();
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowDate(now);
        record.setDueDate(LocalDate.now().plusDays(DEFAULT_BORROW_DAYS));
        record.setStatus(BORROWING);
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        borrowRecordMapper.insert(record);

        int nextStock = book.getStock() - 1;
        book.setStock(nextStock);
        book.setIsAvailable(nextStock > 0);
        book.setUpdatedAt(now);
        bookMapper.updateById(book);

        return convertToDTO(record);
    }

    @Override
    @Transactional
    public BorrowRecordDTO returnBook(Integer recordId, Integer userId, boolean admin) {
        if (recordId == null || userId == null) {
            throw new IllegalArgumentException("Record ID and user ID cannot be empty");
        }

        BorrowRecord record = borrowRecordMapper.selectById(recordId);
        if (record == null) {
            throw new IllegalArgumentException("Borrow record does not exist");
        }
        if (!admin && !Objects.equals(record.getUserId(), userId)) {
            throw new IllegalArgumentException("You can only return your own borrowed books");
        }
        if (!BORROWING.equals(record.getStatus())) {
            throw new IllegalArgumentException("This book has already been returned");
        }

        LocalDateTime now = LocalDateTime.now();
        record.setStatus(RETURNED);
        record.setReturnDate(now);
        record.setUpdatedAt(now);
        borrowRecordMapper.updateById(record);

        Book book = bookMapper.selectById(record.getBookId());
        if (book != null) {
            book.setStock((book.getStock() == null ? 0 : book.getStock()) + 1);
            book.setIsAvailable(true);
            book.setUpdatedAt(now);
            bookMapper.updateById(book);
        }

        return convertToDTO(record);
    }

    @Override
    public List<BorrowRecordDTO> getUserRecords(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        return borrowRecordMapper.selectUserRecordDTOs(userId);
    }

    @Override
    public List<BorrowRecordDTO> getAllRecords() {
        return borrowRecordMapper.selectAllRecordDTOs();
    }

    private boolean hasActiveBorrowRecord(Integer userId, Integer bookId) {
        Long count = borrowRecordMapper.selectCount(
                new QueryWrapper<BorrowRecord>()
                        .eq("user_id", userId)
                        .eq("book_id", bookId)
                        .eq("status", BORROWING)
        );
        return count != null && count > 0;
    }

    private BorrowRecordDTO convertToDTO(BorrowRecord record) {
        Book book = bookMapper.selectById(record.getBookId());
        User user = userMapper.selectById(record.getUserId());

        return BorrowRecordDTO.builder()
                .recordId(record.getRecordId())
                .userId(record.getUserId())
                .username(user == null ? null : user.getUsername())
                .bookId(record.getBookId())
                .bookTitle(book == null ? null : book.getTitle())
                .bookAuthor(book == null ? null : book.getAuthor())
                .borrowDate(record.getBorrowDate())
                .dueDate(record.getDueDate())
                .returnDate(record.getReturnDate())
                .status(record.getStatus())
                .notes(record.getNotes())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
