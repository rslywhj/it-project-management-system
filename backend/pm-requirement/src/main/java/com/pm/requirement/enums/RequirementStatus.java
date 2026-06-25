package com.pm.requirement.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

/**
 * 需求状态枚举
 */
@Getter
@AllArgsConstructor
public enum RequirementStatus {

    DRAFT("draft", "草稿"),
    REVIEWING("reviewing", "评审中"),
    APPROVED("approved", "已通过"),
    REJECTED("rejected", "已驳回"),
    SCHEDULED("scheduled", "已排期"),
    IN_PROGRESS("in_progress", "进行中"),
    DONE("done", "已完成");

    private final String code;
    private final String label;

    /**
     * 获取状态允许流转到的下一状态
     */
    public static Set<String> getAllowedTransitions(String currentStatus) {
        return switch (currentStatus) {
            case "draft" -> Set.of("reviewing");
            case "reviewing" -> Set.of("approved", "rejected");
            case "approved" -> Set.of("scheduled");
            case "rejected" -> Set.of("draft");
            case "scheduled" -> Set.of("in_progress");
            case "in_progress" -> Set.of("done");
            case "done" -> Set.of();
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
