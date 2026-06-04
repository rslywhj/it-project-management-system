package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "配置基线详情")
public class ConfigBaselineVO {

    @Schema(description = "基线ID")
    private Long id;

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "配置项键")
    private String configKey;

    @Schema(description = "标准配置值")
    private String configValue;

    @Schema(description = "配置说明")
    private String description;

    @Schema(description = "是否锁定")
    private Integer isLocked;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
