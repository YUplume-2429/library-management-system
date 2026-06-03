package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Book data mapper.
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {

    @Select("""
            SELECT *
            FROM book
            WHERE title LIKE CONCAT('%', #{keyword}, '%')
            ORDER BY created_at DESC
            """)
    List<Book> selectByTitleLike(String keyword);

    @Select("""
            SELECT *
            FROM book
            WHERE is_available = TRUE
            ORDER BY created_at DESC
            """)
    List<Book> selectAllAvailable();

    @Select("""
            SELECT *
            FROM book
            WHERE category = #{category}
            ORDER BY created_at DESC
            """)
    List<Book> selectByCategory(String category);

    @Select("""
            SELECT DISTINCT category
            FROM book
            WHERE category IS NOT NULL AND TRIM(category) <> ''
            ORDER BY category
            """)
    List<String> selectDistinctCategories();
}
