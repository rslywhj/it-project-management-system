package com.pm.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "测试计划请求")
public class TestPlanRequest {

    @NotBlank(message = "计划名称不能为空")
    @Schema(description = "计划名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "计划说明")
    private String description;

    @Schema(description = "类型（functional/performance/security/regression）")
    private String type;

    @Schema(description = "关联迭代/版本")
    private String iteration;

    @Schema(description = "计划开始日期")
    private LocalDate startDate;

    @Schema(description = "计划结束日期")
    private LocalDate endDate;
}
