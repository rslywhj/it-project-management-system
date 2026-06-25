package com.pm.resource.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("timesheet")
@Schema(description = "工时记录")
public class Timesheet extends BaseEntity {

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "关联任务ID")
    private Long taskId;

    @Schema(description = "工作日期")
    private LocalDate workDate;

    @Schema(description = "工时（小时）")
    private BigDecimal hours;

    @Schema(description = "工作描述")
    private String description;

    @Schema(description = "状态（draft/submitted/approved/rejected）")
    private String status;

    @Schema(description = "审批人ID")
    private Long approvedBy;

    @Schema(description = "审批时间")
    private LocalDateTime approvedAt;

    @Schema(description = "驳回原因")
    private String rejectReason;
}
