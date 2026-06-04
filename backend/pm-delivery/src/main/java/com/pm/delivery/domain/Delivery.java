package com.pm.delivery.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("delivery")
@Schema(description = "交付物")
public class Delivery extends BaseEntity {

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "关联里程碑ID")
    private Long milestoneId;

    @Schema(description = "关联推广单元ID")
    private Long promotionUnitId;

    @Schema(description = "交付物名称")
    private String name;

    @Schema(description = "类型（document/code/test_report/other）")
    private String type;

    @Schema(description = "交付物说明")
    private String description;

    @Schema(description = "状态（draft/submitted/approved/rejected）")
    private String status;

    @Schema(description = "文件存储路径")
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
}
