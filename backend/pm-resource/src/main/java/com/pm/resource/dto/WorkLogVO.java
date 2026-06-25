package com.pm.resource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "工时记录详情")
public class WorkLogVO {

    @Schema(description = "工时记录ID")
    private Long id;

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "任务ID")
    private Long taskId;

    @Schema(description = "任务标题")
    private String taskTitle;

    @Schema(description = "工作日期")
    private LocalDate workDate;

    @Schema(description = "工时（小时）")
    private BigDecimal hours;

    @Schema(description = "工作类型")
    private String workType;

    @Schema(description = "工作描述")
    private String description;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
