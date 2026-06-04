package com.pm.risk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 问题优先级枚举
 */
@Getter
@AllArgsConstructor
public enum IssuePriority {

    CRITICAL("critical", "紧急"),
    HIGH("high", "高"),
    MEDIUM("medium", "中"),
    LOW("low", "低");

    private final String code;
    private final String label;
}
