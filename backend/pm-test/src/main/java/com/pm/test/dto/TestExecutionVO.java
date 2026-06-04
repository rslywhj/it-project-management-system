package com.pm.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "测试执行详情")
public class TestExecutionVO {

    @Schema(description = "执行ID")
    private Long id;

    @Schema(description = "测试计划ID")
    private Long testPlanId;

    @Schema(description = "测试用例ID")
    private Long testCaseId;

    @Schema(description = "用例标题")
    private String caseTitle;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "执行人ID")
    private Long executedBy;

    @Schema(description = "执行时间")
    private LocalDateTime executedAt;

    @Schema(description = "实际结果")
    private String actualResult;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "关联缺陷ID")
    private Long bugId;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
