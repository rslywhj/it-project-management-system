package com.pm.test.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("test_plan")
@Schema(description = "测试计划")
public class TestPlan extends BaseEntity {

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "计划名称")
    private String name;

    @Schema(description = "计划说明")
    private String description;

    @Schema(description = "类型（functional/performance/security/regression）")
    private String type;

    @Schema(description = "状态（draft/in_progress/completed/cancelled）")
    private String status;

    @Schema(description = "关联迭代/版本")
    private String iteration;

    @Schema(description = "计划开始日期")
    private LocalDate startDate;

    @Schema(description = "计划结束日期")
    private LocalDate endDate;

    @Schema(description = "用例总数")
    private Integer totalCases;

    @Schema(description = "已执行用例数")
    private Integer executedCases;

    @Schema(description = "通过用例数")
    private Integer passedCases;

    @Schema(description = "失败用例数")
    private Integer failedCases;

    @Schema(description = "阻塞用例数")
    private Integer blockedCases;

    @Schema(description = "通过率")
    private BigDecimal passRate;
}
