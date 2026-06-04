package com.pm.risk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "风险请求")
public class RiskRequest {

    @NotBlank(message = "风险标题不能为空")
    @Schema(description = "风险标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "风险描述")
    private String description;

    @Schema(description = "类别（technical/resource/schedule/quality/external）")
    private String category;

    @Schema(description = "发生概率（high/medium/low）")
    private String probability;

    @Schema(description = "影响程度（high/medium/low）")
    private String impact;

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
}
