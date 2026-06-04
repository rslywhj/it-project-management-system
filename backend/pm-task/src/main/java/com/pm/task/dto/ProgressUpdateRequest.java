package com.pm.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "更新任务进度请求")
public class ProgressUpdateRequest {

    @DecimalMin(value = "0", message = "完成率不能小于0")
    @DecimalMax(value = "100", message = "完成率不能大于100")
    @Schema(description = "完成百分比（0-100）", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal completionRate;

    @Schema(description = "进度备注")
    private String remark;
}
