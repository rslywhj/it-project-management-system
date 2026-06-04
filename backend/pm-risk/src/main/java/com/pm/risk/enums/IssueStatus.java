package com.pm.risk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

/**
 * 问题状态枚举
 */
@Getter
@AllArgsConstructor
public enum IssueStatus {

    OPEN("open", "待处理"),
    IN_PROGRESS("in_progress", "处理中"),
    RESOLVED("resolved", "已解决"),
    CLOSED("closed", "已关闭"),
    REOPEN("reopen", "重新打开");

    private final String code;
    private final String label;

    /**
     * 获取状态允许流转到的下一状态
     */
    public static Set<String> getAllowedTransitions(String currentStatus) {
        return switch (currentStatus) {
            case "open" -> Set.of("in_progress", "closed");
            case "in_progress" -> Set.of("resolved", "closed");
            case "resolved" -> Set.of("closed", "reopen");
            case "closed" -> Set.of("reopen");
            case "reopen" -> Set.of("in_progress", "closed");
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
