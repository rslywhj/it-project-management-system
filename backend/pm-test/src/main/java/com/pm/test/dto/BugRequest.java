package com.pm.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "缺陷请求")
public class BugRequest {

    @NotBlank(message = "缺陷标题不能为空")
    @Schema(description = "缺陷标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "缺陷描述")
    private String description;

    @Schema(description = "复现步骤")
    private String stepsToReproduce;

    @Schema(description = "预期结果")
    private String expectedResult;

    @Schema(description = "实际结果")
    private String actualResult;

    @Schema(description = "严重程度（critical/major/minor/trivial）")
    private String severity;

    @Schema(description = "优先级")
    private String priority;

    @Schema(description = "类型（functional/ui/performance/security/other）")
    private String type;

    @Schema(description = "指派给")
    private Long assignedTo;

    @Schema(description = "关联测试计划ID")
    private Long testPlanId;

    @Schema(description = "关联测试用例ID")
    private Long testCaseId;

    @Schema(description = "关联需求ID")
    private Long requirementId;

    @Schema(description = "环境信息")
    private String environment;
}
