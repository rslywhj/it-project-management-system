package com.pm.task.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskStatus {

    TODO("todo", "待办"),
    IN_PROGRESS("in_progress", "进行中"),
    DONE("done", "已完成");

    private final String code;
    private final String label;
}
