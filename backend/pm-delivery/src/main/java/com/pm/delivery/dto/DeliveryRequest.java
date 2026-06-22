package com.pm.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "交付物请求")
public class DeliveryRequest {

    @NotBlank(message = "交付物名称不能为空")
    @Size(max = 200, message = "交付物名称不能超过200个字符")
    @Schema(description = "交付物名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Pattern(regexp = "^(document|code|config|data|other)$", message = "类型必须为 document/code/config/data/other")
    @Schema(description = "类型（document/code/test_report/other）")
    private String type;

    @Schema(description = "交付物说明")
    private String description;

    @Schema(description = "关联里程碑ID")
    private Long milestoneId;

    @Schema(description = "关联推广单元ID")
    private Long promotionUnitId;

    @Schema(description = "文件存储路径")
    private String filePath;
}
