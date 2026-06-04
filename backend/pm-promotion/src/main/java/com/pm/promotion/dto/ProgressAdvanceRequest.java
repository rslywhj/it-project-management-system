package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "推进进度请求")
public class ProgressAdvanceRequest {

    @Schema(description = "完成百分比")
    private BigDecimal completionRate;

    @Schema(description = "备注")
    private String remark;
}
