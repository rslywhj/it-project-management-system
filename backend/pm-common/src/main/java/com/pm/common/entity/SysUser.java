package com.pm.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@Schema(description = "系统用户")
public class SysUser extends BaseEntity {

    @Schema(description = "用户名（登录账号）")
    private String username;

    @Schema(description = "密码（BCrypt加密）")
    private String password;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "所属组织ID")
    private Long orgId;

    @Schema(description = "状态（active/disabled/locked）")
    private String status;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginAt;

    @Schema(description = "最后登录IP")
    private String lastLoginIp;

    @Schema(description = "备注")
    private String remark;
}

# SysRole entity
cat > /tmp/it-project-management-system/backend/pm-common/src/main/java/com/pm/common/entity/SysRole.java << 'EOF'
package com.pm.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
@Schema(description = "系统角色")
public class SysRole extends BaseEntity {

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色说明")
    private String description;

    @Schema(description = "数据范围（all/project/promotion_unit/self）")
    private String dataScope;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    @Schema(description = "状态（active/disabled）")
    private String status;

    @Schema(description = "是否系统内置角色")
    private Integer isSystem;
}

# SysUserRole entity
cat > /tmp/it-project-management-system/backend/pm-common/src/main/java/com/pm/common/entity/SysUserRole.java << 'EOF'
package com.pm.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user_role")
@Schema(description = "用户角色关联")
public class SysUserRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "项目ID（项目级角色时有值）")
    private Long projectId;

    @Schema(description = "状态")
    private String status;

    private LocalDateTime createdAt;
    private Long createdBy;
}

# SysPermission entity
cat > /tmp/it-project-management-system/backend/pm-common/src/main/java/com/pm/common/entity/SysPermission.java << 'EOF'
package com.pm.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_permission")
@Schema(description = "系统权限")
public class SysPermission {

    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "权限码")
    private String permissionCode;

    @Schema(description = "权限名称")
    private String permissionName;

    @Schema(description = "所属模块")
    private String module;

    @Schema(description = "操作类型")
    private String operation;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    private LocalDateTime createdAt;
    private Integer isDeleted;
}

# SysRolePermission entity
cat > /tmp/it-project-management-system/backend/pm-common/src/main/java/com/pm/common/entity/SysRolePermission.java << 'EOF'
package com.pm.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role_permission")
@Schema(description = "角色权限关联")
public class SysRolePermission {

    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "权限ID")
    private Long permissionId;

    private LocalDateTime createdAt;
    private Long createdBy;
}

echo "System entities created"
