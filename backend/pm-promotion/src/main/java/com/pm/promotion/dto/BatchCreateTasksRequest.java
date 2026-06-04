package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "批量创建推广任务请求")
public class BatchCreateTasksRequest {

    @NotEmpty(message = "推广单元ID列表不能为空")
    @Schema(description = "推广单元ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> unitIds;

    @Schema(description = "任务模板列表")
    private List<TaskTemplateItem> taskTemplates;

    @Data
    @Schema(description = "任务模板项")
    public static class TaskTemplateItem {

        @Schema(description = "任务标题")
        private String title;

        @Schema(description = "任务描述")
        private String description;

        @Schema(description = "任务类型")
        private String type;

        @Schema(description = "预计天数")
        private Integer estimatedDays;
    }
}
