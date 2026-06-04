package com.pm.risk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

/**
 * 风险状态枚举
 */
@Getter
@AllArgsConstructor
public enum RiskStatus {

    IDENTIFIED("identified", "已识别"),
    ANALYZING("analyzing", "分析中"),
    MITIGATING("mitigating", "应对中"),
    ACCEPTED("accepted", "已接受"),
    CLOSED("closed", "已关闭");

    private final String code;
    private final String label;

    /**
     * 获取状态允许流转到的下一状态
     */
    public static Set<String> getAllowedTransitions(String currentStatus) {
        return switch (currentStatus) {
            case "identified" -> Set.of("analyzing", "closed");
            case "analyzing" -> Set.of("mitigating", "accepted", "closed");
            case "mitigating" -> Set.of("accepted", "closed");
            case "accepted" -> Set.of("mitigating", "closed");
            case "closed" -> Set.of("identified");
            default -> Set.of();
        };
    }

    /**
     * 校验状态流转是否合法
     */
    public static boolean isValidTransition(String from, String to) {
        return getAllowedTransitions(from).contains(to);
    }
}
