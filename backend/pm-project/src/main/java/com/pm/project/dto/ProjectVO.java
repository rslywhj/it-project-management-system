package com.pm.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "项目详情")
public class ProjectVO {

    @Schema(description = "项目ID")
    private Long id;

    @Schema(description = "项目编码")
    private String projectCode;

    @Schema(description = "项目名称")
    private String name;

    @Schema(description = "项目描述")
    private String description;

    @Schema(description = "项目类型")
    private String type;

    @Schema(description = "项目状态")
    private String status;

    @Schema(description = "是否启用集团推广模块")
    private Boolean promotionEnabled;

    @Schema(description = "计划开始日期")
    private LocalDate startDate;

    @Schema(description = "计划结束日期")
    private LocalDate endDate;

    @Schema(description = "所属组织ID")
    private Long orgId;

    @Schema(description = "项目负责人ID")
    private Long projectManagerId;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "成员数量")
    private Integer memberCount;
}
