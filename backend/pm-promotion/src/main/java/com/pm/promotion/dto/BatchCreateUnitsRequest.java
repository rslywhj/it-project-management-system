package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "批量创建推广单元请求")
public class BatchCreateUnitsRequest {

    @NotEmpty(message = "推广单元列表不能为空")
    @Schema(description = "推广单元列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<PromotionUnitCreateRequest> units;

    @Schema(description = "是否基于阶段模板初始化进度")
    private Boolean initStageProgress;
}
