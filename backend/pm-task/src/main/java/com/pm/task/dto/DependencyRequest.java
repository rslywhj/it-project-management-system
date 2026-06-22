package com.pm.task.dto;

import com.pm.common.validation.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "添加任务依赖请求")
public class DependencyRequest {

    @NotNull(message = "被依赖任务ID不能为空")
    @Schema(description = "被依赖任务ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long dependsOnTaskId;

    @Pattern(regexp = ValidationPatterns.DEPENDENCY_TYPE, message = "依赖类型必须为 FS/FF/SS/SF")
    @Schema(description = "依赖类型：FS/FF/SS/SF", defaultValue = "FS")
    private String dependencyType;
}
