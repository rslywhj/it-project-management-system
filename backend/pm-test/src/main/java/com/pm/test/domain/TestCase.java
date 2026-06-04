package com.pm.test.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("test_case")
@Schema(description = "测试用例")
public class TestCase extends BaseEntity {

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "所属模块")
    private String module;

    @Schema(description = "用例标题")
    private String title;

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

    @Schema(description = "状态（active/deprecated/draft）")
    private String status;

    @Schema(description = "关联需求ID")
    private Long requirementId;
}
