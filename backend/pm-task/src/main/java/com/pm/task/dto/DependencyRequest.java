package com.pm.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "添加任务依赖请求")
public class DependencyRequest {

    @NotNull(message = "被依赖任务ID不能为空")
    @Schema(description = "被依赖任务ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long dependsOnTaskId;

    @Schema(description = "依赖类型：FS/FF/SS/SF", defaultValue = "FS")
    private String dependencyType;
}
