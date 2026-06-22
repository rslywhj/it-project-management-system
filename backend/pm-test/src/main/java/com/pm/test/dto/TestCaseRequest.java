package com.pm.test.dto;

import com.pm.common.validation.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "测试用例请求")
public class TestCaseRequest {

    @NotBlank(message = "用例标题不能为空")
    @Size(max = 200, message = "用例标题不能超过200个字符")
    @Schema(description = "用例标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "所属模块")
    private String module;

    @Schema(description = "用例描述")
    private String description;

    @Schema(description = "前置条件")
    private String precondition;

    @Schema(description = "测试步骤（JSON格式）")
    private String steps;

    @Schema(description = "预期结果")
    private String expectedResult;

    @Pattern(regexp = ValidationPatterns.PRIORITY, message = "优先级必须为 critical/high/medium/low")
    @Schema(description = "优先级（critical/high/medium/low）")
    private String priority;

    @Pattern(regexp = "^(functional|performance|security|usability|regression|other)$", message = "类型必须为 functional/performance/security/usability/regression/other")
    @Schema(description = "类型（functional/api/ui/performance）")
    private String type;

    @Schema(description = "关联需求ID")
    private Long requirementId;
}
