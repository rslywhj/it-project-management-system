package com.pm.promotion.dto;

import com.pm.common.validation.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "更新推广进度请求")
public class ProgressUpdateRequest {

    @NotBlank(message = "目标状态不能为空")
    @Pattern(regexp = ValidationPatterns.PROMOTION_PROGRESS_STATUS, message = "目标状态必须为 in_progress/completed/skipped")
    @Schema(description = "目标状态（in_progress/completed/skipped）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(description = "完成百分比")
    private BigDecimal completionRate;

    @Schema(description = "备注")
    private String remark;
}
