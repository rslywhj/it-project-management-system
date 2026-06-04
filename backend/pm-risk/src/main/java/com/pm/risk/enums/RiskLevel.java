package com.pm.risk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 风险等级枚举
 */
@Getter
@AllArgsConstructor
public enum RiskLevel {

    CRITICAL("critical", "严重"),
    HIGH("high", "高"),
    MEDIUM("medium", "中"),
    LOW("low", "低");

    private final String code;
    private final String label;
}
