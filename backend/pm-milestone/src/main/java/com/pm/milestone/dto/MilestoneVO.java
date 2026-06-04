package com.pm.milestone.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "里程碑详情")
public class MilestoneVO {

    @Schema(description = "里程碑ID")
    private Long id;

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "里程碑名称")
    private String name;

    @Schema(description = "里程碑说明")
    private String description;

    @Schema(description = "计划日期")
    private LocalDate plannedDate;

    @Schema(description = "实际完成日期")
    private LocalDate actualDate;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
