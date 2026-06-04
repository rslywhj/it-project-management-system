package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "创建推广单元请求")
public class PromotionUnitCreateRequest {

    @NotBlank(message = "成员单位名称不能为空")
    @Schema(description = "成员单位名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orgName;

    @NotBlank(message = "成员单位编码不能为空")
    @Schema(description = "成员单位编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orgCode;

    @Schema(description = "负责人ID")
    private Long responsibleUserId;

    @Schema(description = "计划开始日期")
    private LocalDate expectedStartDate;

    @Schema(description = "计划结束日期")
    private LocalDate expectedEndDate;

    @Schema(description = "备注")
    private String remark;
}
