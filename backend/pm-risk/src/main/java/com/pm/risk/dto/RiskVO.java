package com.pm.risk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "风险详情")
public class RiskVO {

    @Schema(description = "风险ID")
    private Long id;

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "风险标题")
    private String title;

    @Schema(description = "风险描述")
    private String description;

    @Schema(description = "类别")
    private String category;

    @Schema(description = "发生概率")
    private String probability;

    @Schema(description = "影响程度")
    private String impact;

    @Schema(description = "风险等级")
    private String riskLevel;

    @Schema(description = "状态")
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

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
