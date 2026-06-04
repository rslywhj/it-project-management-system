package com.pm.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "测试用例详情")
public class TestCaseVO {

    @Schema(description = "用例ID")
    private Long id;

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "所属模块")
    private String module;

    @Schema(description = "用例标题")
    private String title;

    @Schema(description = "用例描述")
    private String description;

    @Schema(description = "前置条件")
    private String precondition;

    @Schema(description = "测试步骤")
    private String steps;

    @Schema(description = "预期结果")
    private String expectedResult;

    @Schema(description = "优先级")
    private String priority;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "关联需求ID")
    private Long requirementId;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
