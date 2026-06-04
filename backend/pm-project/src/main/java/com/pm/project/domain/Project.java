package com.pm.project.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 项目实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("project")
@Schema(description = "项目")
public class Project extends BaseEntity {

    @Schema(description = "项目编码")
    private String projectCode;

    @Schema(description = "项目名称")
    private String name;

    @Schema(description = "项目描述")
    private String description;

    @Schema(description = "项目类型：software_dev/system_integration/ops")
    private String type;

    @Schema(description = "项目状态：planning/in_progress/closed")
    private String status;

    @Schema(description = "是否启用集团推广模块")
    private Boolean promotionEnabled;

    @Schema(description = "计划开始日期")
    private LocalDate startDate;

    @Schema(description = "计划结束日期")
    private LocalDate endDate;

    @Schema(description = "所属组织ID")
    private Long orgId;

    @Schema(description = "创建人ID")
    private Long ownerUserId;
}
