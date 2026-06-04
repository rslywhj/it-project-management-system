package com.pm.test.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("test_execution")
@Schema(description = "测试执行")
public class TestExecution extends BaseEntity {

    @Schema(description = "测试计划ID")
    private Long testPlanId;

    @Schema(description = "测试用例ID")
    private Long testCaseId;

    @Schema(description = "状态（pending/passed/failed/blocked/skipped）")
    private String status;

    @Schema(description = "执行人ID")
    private Long executedBy;

    @Schema(description = "执行时间")
    private LocalDateTime executedAt;

    @Schema(description = "实际结果")
    private String actualResult;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "关联缺陷ID")
    private Long bugId;
}
