package com.pm.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pm.common.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 根据用户ID查询权限码列表
     */
    @Select("SELECT DISTINCT p.permission_code FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.is_deleted = 0")
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询角色码列表
     */
    @Select("SELECT DISTINCT r.role_code FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.is_deleted = 0")
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
}
