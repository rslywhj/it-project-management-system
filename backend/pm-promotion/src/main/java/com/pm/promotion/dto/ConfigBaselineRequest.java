package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "配置基线请求")
public class ConfigBaselineRequest {

    @NotBlank(message = "配置项键不能为空")
    @Schema(description = "配置项键", requiredMode = Schema.RequiredMode.REQUIRED)
    private String configKey;

    @Schema(description = "标准配置值")
    private String configValue;

    @Schema(description = "配置说明")
    private String description;

    @Schema(description = "是否锁定")
    private Integer isLocked;
}
