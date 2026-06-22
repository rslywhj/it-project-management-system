package com.pm.milestone.dto;

import com.pm.common.validation.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "更新里程碑请求")
public class MilestoneUpdateRequest {

    @Schema(description = "里程碑名称")
    private String name;

    @Schema(description = "里程碑说明")
    private String description;

    @Schema(description = "计划日期")
    private LocalDate plannedDate;

    @Schema(description = "实际完成日期")
    private LocalDate actualDate;

    @Pattern(regexp = ValidationPatterns.MILESTONE_STATUS, message = "状态必须为 pending/in_progress/at_risk/completed/delayed")
    @Schema(description = "状态（pending/in_progress/at_risk/completed/delayed）")
    private String status;

    @Schema(description = "排序序号")
    private Integer sortOrder;
}
