package com.pm.risk.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("issue")
@Schema(description = "问题")
public class Issue extends BaseEntity {

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "问题标题")
    private String title;

    @Schema(description = "问题描述")
    private String description;

    @Schema(description = "类别（technical/process/resource/communication/other）")
    private String category;

    @Schema(description = "严重程度（critical/major/minor/trivial）")
    private String severity;

    @Schema(description = "状态（open/in_progress/resolved/closed/reopen）")
    private String status;

    @Schema(description = "指派给")
    private Long assignedTo;

    @Schema(description = "报告人ID")
    private Long reportedBy;

    @Schema(description = "解决方案")
    private String resolution;

    @Schema(description = "解决时间")
    private LocalDateTime resolvedAt;

    @Schema(description = "解决人ID")
    private Long resolvedBy;

    @Schema(description = "关闭时间")
    private LocalDateTime closedAt;

    @Schema(description = "期望解决日期")
    private LocalDate dueDate;
}
