package com.pm.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "任务详情")
public class TaskVO {

    @Schema(description = "任务ID")
    private Long id;

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "父任务ID")
    private Long parentTaskId;

    @Schema(description = "关联需求ID")
    private Long requirementId;

    @Schema(description = "关联推广单元ID")
    private Long promotionUnitId;

    @Schema(description = "任务标题")
    private String title;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "任务类型")
    private String type;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "优先级")
    private String priority;

    @Schema(description = "负责人ID")
    private Long assignedTo;

    @Schema(description = "计划开始日期")
    private LocalDate plannedStart;

    @Schema(description = "计划结束日期")
    private LocalDate plannedEnd;

    @Schema(description = "实际开始日期")
    private LocalDate actualStart;

    @Schema(description = "实际结束日期")
    private LocalDate actualEnd;

    @Schema(description = "完成百分比")
    private BigDecimal completionRate;

    @Schema(description = "WBS编码")
    private String wbsCode;

    @Schema(description = "子任务列表")
    private List<TaskVO> children;

    @Schema(description = "依赖的任务ID列表")
    private List<Long> dependsOnTaskIds;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
