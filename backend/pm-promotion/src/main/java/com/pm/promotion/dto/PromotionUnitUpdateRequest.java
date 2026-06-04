package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "更新推广单元请求")
public class PromotionUnitUpdateRequest {

    @Size(max = 200, message = "成员单位名称不能超过200个字符")
    @Schema(description = "成员单位名称")
    private String orgName;

    @Schema(description = "推广单元负责人ID")
    private Long responsibleUserId;

    @Schema(description = "计划开始日期")
    private LocalDate expectedStartDate;

    @Schema(description = "计划结束日期")
    private LocalDate expectedEndDate;

    @Schema(description = "备注")
    private String remark;
}
