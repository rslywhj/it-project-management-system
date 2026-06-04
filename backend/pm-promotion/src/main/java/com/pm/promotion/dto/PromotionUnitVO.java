package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "推广单元详情")
public class PromotionUnitVO {

    @Schema(description = "推广单元ID")
    private Long id;

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "成员单位名称")
    private String orgName;

    @Schema(description = "成员单位编码")
    private String orgCode;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "当前推广阶段ID")
    private Long currentStageId;

    @Schema(description = "当前阶段名称")
    private String currentStageName;

    @Schema(description = "完成百分比")
    private BigDecimal completionRate;

    @Schema(description = "推广单元负责人ID")
    private Long responsibleUserId;

    @Schema(description = "计划开始日期")
    private LocalDate expectedStartDate;

    @Schema(description = "计划结束日期")
    private LocalDate expectedEndDate;

    @Schema(description = "实际开始日期")
    private LocalDate actualStartDate;

    @Schema(description = "实际结束日期")
    private LocalDate actualEndDate;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "阶段进度列表")
    private List<PromotionProgressVO> progressList;
}
