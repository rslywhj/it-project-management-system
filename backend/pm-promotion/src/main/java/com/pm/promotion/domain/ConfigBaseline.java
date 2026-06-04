package com.pm.promotion.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("config_baseline")
@Schema(description = "配置基线")
public class ConfigBaseline extends BaseEntity {

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "配置项键")
    private String configKey;

    @Schema(description = "标准配置值")
    private String configValue;

    @Schema(description = "配置说明")
    private String description;

    @Schema(description = "是否锁定（不可由下级覆盖）")
    private Integer isLocked;
}
