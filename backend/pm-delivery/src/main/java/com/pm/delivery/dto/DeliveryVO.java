package com.pm.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "交付物详情")
public class DeliveryVO {

    @Schema(description = "交付物ID")
    private Long id;

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "关联里程碑ID")
    private Long milestoneId;

    @Schema(description = "关联推广单元ID")
    private Long promotionUnitId;

    @Schema(description = "交付物名称")
    private String name;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "说明")
    private String description;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "文件路径")
    private String filePath;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "提交人ID")
    private Long submittedBy;

    @Schema(description = "审核人ID")
    private Long reviewedBy;

    @Schema(description = "审核时间")
    private LocalDateTime reviewedAt;

    @Schema(description = "审核意见")
    private String reviewComment;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
