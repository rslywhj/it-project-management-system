package com.pm.promotion.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("promotion_unit")
@Schema(description = "推广单元")
public class PromotionUnit extends BaseEntity {

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "成员单位名称")
    private String orgName;

    @Schema(description = "成员单位编码")
    private String orgCode;

    @Schema(description = "状态（pending/in_progress/completed/suspended）")
    private String status;

    @Schema(description = "当前推广阶段ID")
    private Long currentStageId;

    @Schema(description = "完成百分比")
    private BigDecimal completionRate;

    @Schema(description = "推广单元负责人ID")
    private Long responsibleUserId;

    @Schema(description = "计划开始日期")
    private LocalDate expectedStartDate;

    @Schema(description = "计划结束日期")
    private LocalDate expectedEndDate;

    @Schema(description = "实际开始日期")
    private LocalDate actualStartDate;

    @Schema(description = "实际结束日期")
    private LocalDate actualEndDate;

    @Schema(description = "备注")
    private String remark;
}
