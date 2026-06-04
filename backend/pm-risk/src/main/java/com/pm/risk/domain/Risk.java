package com.pm.risk.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("risk")
@Schema(description = "风险")
public class Risk extends BaseEntity {

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "风险标题")
    private String title;

    @Schema(description = "风险描述")
    private String description;

    @Schema(description = "类别（technical/resource/schedule/quality/external）")
    private String category;

    @Schema(description = "发生概率（high/medium/low）")
    private String probability;

    @Schema(description = "影响程度（high/medium/low）")
    private String impact;

    @Schema(description = "风险等级（critical/high/medium/low）")
    private String riskLevel;

    @Schema(description = "状态（open/monitoring/mitigated/closed/realized）")
    private String status;

    @Schema(description = "风险负责人ID")
    private Long ownerId;

    @Schema(description = "应对措施")
    private String mitigationPlan;

    @Schema(description = "应急预案")
    private String contingencyPlan;

    @Schema(description = "识别日期")
    private LocalDate identifiedDate;

    @Schema(description = "复审日期")
    private LocalDate reviewDate;

    @Schema(description = "关闭日期")
    private LocalDate closedDate;
}
