package com.pm.milestone.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("milestone")
@Schema(description = "里程碑")
public class Milestone extends BaseEntity {

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

    @Schema(description = "状态（pending/in_progress/at_risk/completed/delayed）")
    private String status;

    @Schema(description = "排序序号")
    private Integer sortOrder;
}
