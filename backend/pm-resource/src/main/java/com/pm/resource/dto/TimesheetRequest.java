package com.pm.resource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "工时记录请求")
public class TimesheetRequest {

    @Schema(description = "关联任务ID")
    private Long taskId;

    @NotNull(message = "工作日期不能为空")
    @Schema(description = "工作日期")
    private LocalDate workDate;

    @NotNull(message = "工时不能为空")
    @Schema(description = "工时（小时）")
    private BigDecimal hours;

    @Schema(description = "工作描述")
    private String description;
}
