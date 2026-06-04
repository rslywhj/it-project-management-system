package com.pm.risk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "问题请求")
public class IssueRequest {

    @NotBlank(message = "问题标题不能为空")
    @Schema(description = "问题标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "问题描述")
    private String description;

    @Schema(description = "类别（technical/process/resource/communication/other）")
    private String category;

    @Schema(description = "严重程度（critical/major/minor/trivial）")
    private String severity;

    @Schema(description = "指派给")
    private Long assignedTo;

    @Schema(description = "期望解决日期")
    private LocalDate dueDate;
}
