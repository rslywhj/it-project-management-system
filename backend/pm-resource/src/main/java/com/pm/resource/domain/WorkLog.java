package com.pm.resource.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("work_log")
@Schema(description = "工时记录")
public class WorkLog extends BaseEntity {

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "关联任务ID")
    private Long taskId;

    @Schema(description = "工作日期")
    private LocalDate workDate;

    @Schema(description = "工时（小时）")
    private BigDecimal hours;

    @Schema(description = "工作类型（development/testing/meeting/design/review/other）")
    private String workType;

    @Schema(description = "工作描述")
    private String description;
}
