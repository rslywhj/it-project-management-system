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

    /** 需求状态：reviewing / approved / scheduled / done / rejected / draft */
    public static final String REQUIREMENT_STATUS = "^(reviewing|approved|scheduled|done|rejected|draft)$";

    /** 交付物审核动作：approve / reject */
    public static final String DELIVERY_ACTION = "^(approve|reject)$";

    /** 里程碑状态：pending / in_progress / at_risk / completed / delayed */
    public static final String MILESTONE_STATUS = "^(pending|in_progress|at_risk|completed|delayed)$";

    /** 推广进度状态：in_progress / completed / skipped */
    public static final String PROMOTION_PROGRESS_STATUS = "^(in_progress|completed|skipped)$";

    /** 任务依赖类型：FS / FF / SS / SF */
    public static final String DEPENDENCY_TYPE = "^(FS|FF|SS|SF)$";
}
