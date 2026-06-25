package com.pm.notification.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统通知实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_notification")
@Schema(description = "系统通知")
public class SysNotification extends BaseEntity {

    @Schema(description = "接收人ID")
    private Long userId;

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "通知类型：system/task/requirement/promotion/alert")
    private String type;

    @Schema(description = "关联实体类型")
    private String relatedEntityType;

    @Schema(description = "关联实体ID")
    private Long relatedEntityId;

    @Schema(description = "是否已读（0否 1是）")
    private Integer isRead;

    @Schema(description = "阅读时间")
    private LocalDateTime readAt;
}
