package com.pm.promotion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "延期预警")
public class OverdueAlertVO {

    @Schema(description = "推广单元ID")
    private Long unitId;

    @Schema(description = "成员单位名称")
    private String orgName;

    @Schema(description = "当前阶段")
    private String stageName;

    @Schema(description = "预计结束日期")
    private LocalDate expectedEndDate;

    @Schema(description = "延期天数")
    private Integer overdueDays;

    @Schema(description = "预警级别（warning/critical）")
    private String alertLevel;
}
