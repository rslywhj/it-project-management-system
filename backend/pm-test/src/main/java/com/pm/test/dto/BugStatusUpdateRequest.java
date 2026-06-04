package com.pm.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "缺陷状态更新请求")
public class BugStatusUpdateRequest {

    @NotBlank(message = "目标状态不能为空")
    @Schema(description = "目标状态（open/in_progress/resolved/closed/reopen）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(description = "解决方案（resolved时填写）")
    private String resolution;
}
