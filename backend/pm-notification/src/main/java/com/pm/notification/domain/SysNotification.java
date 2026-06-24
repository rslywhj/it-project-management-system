package com.pm.notification.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统通知实体
 */
@Data
@TableName("sys_notification")
public class SysNotification implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 接收人ID */
    private Long userId;

    /** 通知标题 */
    private String title;

    /** 通知内容 */
    private String content;

    /** 通知类型：system/task/requirement/promotion/alert */
    private String type;

    /** 关联实体类型 */
    private String relatedEntityType;

    /** 关联实体ID */
    private Long relatedEntityId;

    /** 是否已读 */
    private Integer isRead;

    /** 阅读时间 */
    private LocalDateTime readAt;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
