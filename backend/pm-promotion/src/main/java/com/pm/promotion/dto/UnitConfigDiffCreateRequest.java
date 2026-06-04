package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "创建配置差异请求")
public class UnitConfigDiffCreateRequest {

    @NotNull(message = "配置基线ID不能为空")
    @Schema(description = "关联配置基线ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long configBaselineId;

    @Schema(description = "差异值")
    private String diffValue;

    @Schema(description = "差异原因")
    private String diffReason;
}
