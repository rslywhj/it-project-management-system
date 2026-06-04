package com.pm.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "交付物审核请求")
public class DeliveryReviewRequest {

    @NotBlank(message = "审核动作不能为空")
    @Schema(description = "审核动作（approve/reject）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String action;

    @Schema(description = "审核意见")
    private String reviewComment;
}
