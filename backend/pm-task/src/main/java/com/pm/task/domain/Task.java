package com.pm.task.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 任务实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("task")
@Schema(description = "任务")
public class Task extends BaseEntity {

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "父任务ID（WBS层级）")
    private Long parentTaskId;

    @Schema(description = "关联需求ID")
    private Long requirementId;

    @Schema(description = "关联推广单元ID")
    private Long promotionUnitId;

    @Schema(description = "任务标题")
    private String title;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "任务类型：dev/test/deploy/training/design/review")
    private String type;

    @Schema(description = "状态：todo/in_progress/done")
    private String status;

    @Schema(description = "优先级：critical/high/medium/low")
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

    @Schema(description = "预估工时（小时）")
    private BigDecimal estimatedHours;

    @Schema(description = "实际工时（小时）")
    private BigDecimal actualHours;

    @Schema(description = "WBS编码")
    private String wbsCode;

    @Schema(description = "排序序号")
    private Integer sortOrder;
}
