package com.pm.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "测试计划详情")
public class TestPlanVO {

    @Schema(description = "计划ID")
    private Long id;

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "计划名称")
    private String name;

    @Schema(description = "计划说明")
    private String description;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "状态")
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

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
