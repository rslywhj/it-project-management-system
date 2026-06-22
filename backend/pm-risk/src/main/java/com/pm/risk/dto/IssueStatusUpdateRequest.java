package com.pm.risk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.pm.common.validation.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "问题状态更新请求")
public class IssueStatusUpdateRequest {

    @NotBlank(message = "目标状态不能为空")
    @Pattern(regexp = ValidationPatterns.ISSUE_STATUS, message = "目标状态必须为 open/in_progress/resolved/closed/reopen")
    @Schema(description = "目标状态（open/in_progress/resolved/closed/reopen）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(description = "解决方案（resolved时填写）")
    private String resolution;
}
