package com.pm.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "测试用例请求")
public class TestCaseRequest {

    @NotBlank(message = "用例标题不能为空")
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

    @Schema(description = "优先级（critical/high/medium/low）")
    private String priority;

    @Schema(description = "类型（functional/api/ui/performance）")
    private String type;

    @Schema(description = "关联需求ID")
    private Long requirementId;
}
