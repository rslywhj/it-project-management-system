package com.pm.risk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "预警详情")
public class WarningVO {

    @Schema(description = "预警ID")
    private Long id;

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "预警类型")
    private String warningType;

    @Schema(description = "关联ID")
    private Long relatedId;

    @Schema(description = "关联类型")
    private String relatedType;

    @Schema(description = "预警消息")
    private String warningMessage;

    @Schema(description = "预警时间")
    private LocalDateTime warningDate;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "确认人ID")
    private Long acknowledgedBy;

    @Schema(description = "确认时间")
    private LocalDateTime acknowledgedAt;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
