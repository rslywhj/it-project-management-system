package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "更新进度请求")
public class ProgressUpdateRequest {

    @Schema(description = "完成百分比")
    private BigDecimal completionRate;

    @Schema(description = "预计结束日期")
    private LocalDate expectedEndDate;

    @Schema(description = "备注")
    private String remark;
}
