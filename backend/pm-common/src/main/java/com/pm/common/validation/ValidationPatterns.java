package com.pm.common.validation;

/**
 * 验证正则表达式常量类
 * 统一定义各模块共用的字段校验正则
 */
public final class ValidationPatterns {

    private ValidationPatterns() {}

    /** 优先级：critical / high / medium / low */
    public static final String PRIORITY = "^(critical|high|medium|low)$";

    /** 需求来源：business / tech / regulatory */
    public static final String REQUIREMENT_SOURCE = "^(business|tech|regulatory)$";

    /** 需求分类：general / differential */
    public static final String REQUIREMENT_CATEGORY = "^(general|differential)$";

    /** 需求状态：draft / reviewing / approved / rejected / scheduled / in_progress / done */
    public static final String REQUIREMENT_STATUS = "^(draft|reviewing|approved|rejected|scheduled|in_progress|done)$";

    /** 交付物审核动作：approve / reject */
    public static final String DELIVERY_ACTION = "^(approve|reject)$";

    /** 里程碑状态：pending / in_progress / at_risk / completed / delayed */
    public static final String MILESTONE_STATUS = "^(pending|in_progress|at_risk|completed|delayed)$";

    /** 推广进度状态：in_progress / completed / skipped */
    public static final String PROMOTION_PROGRESS_STATUS = "^(in_progress|completed|skipped)$";

    /** 任务依赖类型：FS / FF / SS / SF */
    public static final String DEPENDENCY_TYPE = "^(FS|FF|SS|SF)$";

    /** 严重程度：critical / major / minor / trivial */
    public static final String SEVERITY = "^(critical|major|minor|trivial)$";

    /** 问题类别：technical / process / resource / communication / other */
    public static final String ISSUE_CATEGORY = "^(technical|process|resource|communication|other)$";

    /** 问题状态：open / in_progress / resolved / closed / reopen */
    public static final String ISSUE_STATUS = "^(open|in_progress|resolved|closed|reopen)$";

    /** 发生概率：high / medium / low */
    public static final String PROBABILITY = "^(high|medium|low)$";

    /** 影响程度：high / medium / low */
    public static final String IMPACT = "^(high|medium|low)$";

    /** 风险类别：technical / resource / schedule / quality / external */
    public static final String RISK_CATEGORY = "^(technical|resource|schedule|quality|external)$";

    /** 可用状态：available / busy / unavailable / on_leave */
    public static final String AVAILABILITY = "^(available|busy|unavailable|on_leave)$";

    /** 工作类型：development / testing / meeting / design / review / other */
    public static final String WORK_TYPE = "^(development|testing|meeting|design|review|other)$";
}
