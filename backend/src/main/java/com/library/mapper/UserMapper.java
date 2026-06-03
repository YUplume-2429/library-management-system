package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * User data access layer.
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * Find a user by username.
     */
    @Select("SELECT * FROM `user` WHERE username = #{username}")
    User selectByUsername(String username);
}
