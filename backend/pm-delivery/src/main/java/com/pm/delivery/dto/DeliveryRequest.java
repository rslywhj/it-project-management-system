package com.pm.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "交付物请求")
public class DeliveryRequest {

    @NotBlank(message = "交付物名称不能为空")
    @Schema(description = "交付物名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

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
