package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 角色数据访问层
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    
    /**
     * 根据角色名称查询角色
     */
    @Select("SELECT * FROM role WHERE role_name = #{roleName}")
    Role selectByRoleName(String roleName);
}
