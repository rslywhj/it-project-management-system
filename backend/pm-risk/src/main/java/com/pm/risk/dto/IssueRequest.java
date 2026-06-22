package com.pm.risk.dto;

import com.pm.common.validation.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "问题请求")
public class IssueRequest {

    @NotBlank(message = "问题标题不能为空")
    @Size(max = 200, message = "问题标题不能超过200个字符")
    @Schema(description = "问题标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "问题描述")
    private String description;

    @Pattern(regexp = ValidationPatterns.ISSUE_CATEGORY, message = "类别必须为 technical/process/resource/communication/other")
    @Schema(description = "类别（technical/process/resource/communication/other）")
    private String category;

    @Pattern(regexp = ValidationPatterns.SEVERITY, message = "严重程度必须为 critical/major/minor/trivial")
    @Schema(description = "严重程度（critical/major/minor/trivial）")
    private String severity;

    @Schema(description = "指派给")
    private Long assignedTo;

    @Schema(description = "期望解决日期")
    private LocalDate dueDate;
}
