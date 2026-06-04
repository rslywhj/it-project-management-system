package com.pm.milestone.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "创建里程碑请求")
public class MilestoneCreateRequest {

    @NotBlank(message = "里程碑名称不能为空")
    @Schema(description = "里程碑名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "里程碑说明")
    private String description;

    @NotNull(message = "计划日期不能为空")
    @Schema(description = "计划日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate plannedDate;

    @Schema(description = "排序序号")
    private Integer sortOrder;
}
