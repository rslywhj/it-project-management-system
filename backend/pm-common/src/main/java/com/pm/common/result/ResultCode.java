package com.pm.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一结果码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证，请先登录"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "数据冲突"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // 业务错误码 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_DISABLED(1003, "用户已禁用"),
    TOKEN_EXPIRED(1004, "Token已过期"),
    TOKEN_INVALID(1005, "Token无效"),
    PROJECT_NOT_FOUND(1010, "项目不存在"),
    PROJECT_MEMBER_EXISTS(1011, "项目成员已存在"),
    REQUIREMENT_NOT_FOUND(1020, "需求不存在"),
    REQUIREMENT_STATUS_INVALID(1021, "需求状态流转不合法"),
    TASK_NOT_FOUND(1030, "任务不存在"),
    TASK_DEPENDENCY_CYCLE(1031, "任务依赖存在循环"),

    // 推广管理错误码 1xxx
    PROMOTION_UNIT_NOT_FOUND(1040, "推广单元不存在"),
    PROMOTION_UNIT_ORG_EXISTS(1041, "该项目下已存在相同编码的推广单元"),
    STAGE_TEMPLATE_NOT_FOUND(1042, "推广阶段模板不存在"),
    STAGE_TEMPLATE_LOCKED(1043, "该阶段模板已锁定，不可修改"),
    PROGRESS_NOT_FOUND(1044, "推广进度记录不存在"),
    PROGRESS_STAGE_LOCKED(1045, "当前阶段为集团锁定阶段，不可跳过"),
    UNIT_REQUIREMENT_NOT_FOUND(1046, "推广单元需求不存在"),
    CONFIG_BASELINE_NOT_FOUND(1047, "配置基线不存在"),
    CONFIG_BASELINE_KEY_EXISTS(1048, "该项目下已存在相同配置项键"),
    UNIT_CONFIG_DIFF_NOT_FOUND(1049, "配置差异记录不存在"),
    UNIT_CONFIG_DIFF_ALREADY_RESOLVED(1050, "配置差异已审批，不可重复操作"),
    ;

    private final int code;
    private final String message;
}
