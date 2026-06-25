package com.pm.resource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "资源利用率")
public class ResourceUtilizationVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "总分配百分比")
    private Integer totalAllocation;

    @Schema(description = "活跃项目数")
    private Integer activeProjects;

    @Schema(description = "本月总工时")
    private BigDecimal totalHoursThisMonth;

    @Schema(description = "利用率")
    private BigDecimal utilizationRate;
}
