package com.pm.promotion.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 成员单位配置差异实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("unit_config_diff")
@Schema(description = "成员单位配置差异")
public class UnitConfigDiff extends BaseEntity {

    @Schema(description = "推广单元ID")
    private Long promotionUnitId;

    @Schema(description = "关联配置基线ID")
    private Long configBaselineId;

    @Schema(description = "差异值")
    private String diffValue;

    @Schema(description = "差异原因")
    private String diffReason;

    @Schema(description = "状态：pending/approved/rejected")
    private String status;

    @Schema(description = "审批人ID")
    private Long approvedBy;

    @Schema(description = "审批时间")
    private LocalDateTime approvedAt;
}
