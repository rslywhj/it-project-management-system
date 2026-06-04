package com.pm.lowcode.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "流程实例详情")
public class ProcessInstanceVO {

    @Schema(description = "流程实例ID")
    private String processInstanceId;

    @Schema(description = "流程定义Key")
    private String processDefinitionKey;

    @Schema(description = "业务实体ID")
    private Long entityId;

    @Schema(description = "业务实体类型")
    private String entityType;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "发起人ID")
    private Long startedBy;

    @Schema(description = "发起时间")
    private LocalDateTime startedAt;

    @Schema(description = "当前任务列表")
    private List<TaskVO> currentTasks;

    @Data
    @Schema(description = "任务信息")
    public static class TaskVO {
        private String taskId;
        private String taskName;
        private String assignee;
        private LocalDateTime createTime;
        private String formKey;
    }
}
