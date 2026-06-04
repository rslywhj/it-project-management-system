package com.pm.risk.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 预警记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("risk_warning")
@Schema(description = "预警记录")
public class RiskWarning extends BaseEntity {

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "预警类型：risk_expiry/issue_expiry/risk_escalation/issue_escalation")
    private String warningType;

    @Schema(description = "关联ID（风险ID或问题ID）")
    private Long relatedId;

    @Schema(description = "关联类型：risk/issue")
    private String relatedType;

    @Schema(description = "预警消息")
    private String warningMessage;

    @Schema(description = "预警时间")
    private LocalDateTime warningDate;

    @Schema(description = "状态：pending/acknowledged/dismissed")
    private String status;

    @Schema(description = "确认人ID")
    private Long acknowledgedBy;

    @Schema(description = "确认时间")
    private LocalDateTime acknowledgedAt;
}
