package com.pm.task.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 任务依赖关系
 */
@Data
@TableName("task_dependency")
@Schema(description = "任务依赖")
public class TaskDependency {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "任务ID")
    private Long taskId;

    @Schema(description = "被依赖任务ID")
    private Long dependsOnTaskId;

    @Schema(description = "依赖类型：FS(完成-开始)/FF(完成-完成)/SS(开始-开始)/SF(开始-完成)")
    private String dependencyType;
}
