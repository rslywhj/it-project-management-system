package com.pm.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "创建项目请求")
public class ProjectCreateRequest {

    @NotBlank(message = "项目名称不能为空")
    @Size(max = 200, message = "项目名称不能超过200个字符")
    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 50, message = "项目编码不能超过50个字符")
    @Schema(description = "项目编码（不填则自动生成）")
    private String projectCode;

    @Schema(description = "项目描述")
    private String description;

    @Schema(description = "项目类型：software_dev/system_integration/ops")
    private String type;

    @Schema(description = "是否启用集团推广模块", defaultValue = "false")
    private Boolean promotionEnabled;

    @Schema(description = "计划开始日期")
    private LocalDate startDate;

    @Schema(description = "计划结束日期")
    private LocalDate endDate;

    @Schema(description = "所属组织ID")
    private Long orgId;
}
