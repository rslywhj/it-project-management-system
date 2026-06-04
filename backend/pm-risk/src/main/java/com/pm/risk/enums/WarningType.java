package com.pm.risk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 预警类型枚举
 */
@Getter
@AllArgsConstructor
public enum WarningType {

    RISK_EXPIRY("risk_expiry", "风险到期预警"),
    ISSUE_EXPIRY("issue_expiry", "问题到期预警"),
    RISK_ESCALATION("risk_escalation", "风险升级预警"),
    ISSUE_ESCALATION("issue_escalation", "问题升级预警");

    private final String code;
    private final String label;
}
