package com.pm.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "测试执行请求")
public class TestExecutionRequest {

    @NotNull(message = "测试用例ID不能为空")
    @Schema(description = "测试用例ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long testCaseId;

    @Schema(description = "状态（passed/failed/blocked/skipped）")
    private String status;

    @Schema(description = "实际结果")
    private String actualResult;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "关联缺陷ID")
    private Long bugId;
}
