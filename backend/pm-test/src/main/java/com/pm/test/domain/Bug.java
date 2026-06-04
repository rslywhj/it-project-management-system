package com.pm.test.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bug")
@Schema(description = "缺陷")
public class Bug extends BaseEntity {

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "缺陷标题")
    private String title;

    @Schema(description = "缺陷描述")
    private String description;

    @Schema(description = "复现步骤")
    private String stepsToReproduce;

    @Schema(description = "预期结果")
    private String expectedResult;

    @Schema(description = "实际结果")
    private String actualResult;

    @Schema(description = "严重程度（critical/major/minor/trivial）")
    private String severity;

    @Schema(description = "优先级（critical/high/medium/low）")
    private String priority;

    @Schema(description = "状态（open/in_progress/resolved/closed/reopen）")
    private String status;

    @Schema(description = "类型（functional/ui/performance/security/other）")
    private String type;

    @Schema(description = "指派给")
    private Long assignedTo;

    @Schema(description = "报告人ID")
    private Long reportedBy;

    @Schema(description = "关联测试计划ID")
    private Long testPlanId;

    @Schema(description = "关联测试用例ID")
    private Long testCaseId;

    @Schema(description = "关联需求ID")
    private Long requirementId;

    @Schema(description = "解决时间")
    private LocalDateTime resolvedAt;

    @Schema(description = "解决人ID")
    private Long resolvedBy;

    @Schema(description = "解决方案")
    private String resolution;

    @Schema(description = "关闭时间")
    private LocalDateTime closedAt;

    @Schema(description = "环境信息")
    private String environment;

    @Schema(description = "附件路径")
    private String attachmentPath;
}
