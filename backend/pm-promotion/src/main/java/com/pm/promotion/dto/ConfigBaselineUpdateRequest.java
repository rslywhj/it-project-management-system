package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新配置基线请求")
public class ConfigBaselineUpdateRequest {

    @Schema(description = "标准配置值")
    private String configValue;

    @Schema(description = "配置说明")
    private String description;

    @Schema(description = "是否锁定")
    private Boolean isLocked;
}
