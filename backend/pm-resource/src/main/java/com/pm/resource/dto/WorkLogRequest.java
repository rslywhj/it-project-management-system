package com.pm.resource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "工时记录请求")
public class WorkLogRequest {

    @NotNull(message = "工作日期不能为空")
    @Schema(description = "工作日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate workDate;

    @NotNull(message = "工时不能为空")
    @Schema(description = "工时（小时）", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal hours;

    @Schema(description = "关联任务ID")
    private Long taskId;

    @Pattern(regexp = "^(development|testing|design|meeting|support|other)$", message = "工作类型必须为 development/testing/design/meeting/support/other")
    @Schema(description = "工作类型（development/testing/meeting/design/review/other）")
    private String workType;

    @Schema(description = "工作描述")
    private String description;
}
