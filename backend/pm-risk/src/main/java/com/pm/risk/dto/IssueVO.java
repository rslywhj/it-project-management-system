package com.pm.risk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "问题详情")
public class IssueVO {

    @Schema(description = "问题ID")
    private Long id;

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "问题标题")
    private String title;

    @Schema(description = "问题描述")
    private String description;

    @Schema(description = "类别")
    private String category;

    @Schema(description = "严重程度")
    private String severity;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "指派给")
    private Long assignedTo;

    @Schema(description = "报告人ID")
    private Long reportedBy;

    @Schema(description = "解决方案")
    private String resolution;

    @Schema(description = "解决时间")
    private LocalDateTime resolvedAt;

    @Schema(description = "关闭时间")
    private LocalDateTime closedAt;

    @Schema(description = "期望解决日期")
    private LocalDate dueDate;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
