package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.dto.BorrowRecordDTO;
import com.library.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 借阅记录数据访问层
 */
@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {

    @Select("""
            SELECT
                br.record_id AS recordId,
                br.user_id AS userId,
                u.username AS username,
                br.book_id AS bookId,
                b.title AS bookTitle,
                b.author AS bookAuthor,
                br.borrow_date AS borrowDate,
                br.due_date AS dueDate,
                br.return_date AS returnDate,
                br.status AS status,
                br.notes AS notes,
                br.created_at AS createdAt,
                br.updated_at AS updatedAt
            FROM borrow_record br
            LEFT JOIN `user` u ON br.user_id = u.user_id
            LEFT JOIN book b ON br.book_id = b.book_id
            ORDER BY br.created_at DESC
            """)
    List<BorrowRecordDTO> selectAllRecordDTOs();

    @Select("""
            SELECT
                br.record_id AS recordId,
                br.user_id AS userId,
                u.username AS username,
                br.book_id AS bookId,
                b.title AS bookTitle,
                b.author AS bookAuthor,
                br.borrow_date AS borrowDate,
                br.due_date AS dueDate,
                br.return_date AS returnDate,
                br.status AS status,
                br.notes AS notes,
                br.created_at AS createdAt,
                br.updated_at AS updatedAt
            FROM borrow_record br
            LEFT JOIN `user` u ON br.user_id = u.user_id
            LEFT JOIN book b ON br.book_id = b.book_id
            WHERE br.user_id = #{userId}
            ORDER BY br.created_at DESC
            """)
    List<BorrowRecordDTO> selectUserRecordDTOs(@Param("userId") Integer userId);
}
