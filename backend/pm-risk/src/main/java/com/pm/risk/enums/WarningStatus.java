package com.pm.risk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 预警状态枚举
 */
@Getter
@AllArgsConstructor
public enum WarningStatus {

    PENDING("pending", "待处理"),
    ACKNOWLEDGED("acknowledged", "已确认"),
    DISMISSED("dismissed", "已忽略");

    private final String code;
    private final String label;
}
