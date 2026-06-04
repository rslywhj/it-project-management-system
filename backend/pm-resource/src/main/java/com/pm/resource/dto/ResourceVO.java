package com.pm.resource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "资源详情")
public class ResourceVO {

    @Schema(description = "资源ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "技能标签")
    private String skillTags;

    @Schema(description = "可用状态")
    private String availability;

    @Schema(description = "当前工作负载百分比")
    private Integer workloadPercent;

    @Schema(description = "每周可用工时")
    private BigDecimal capacityHoursPerWeek;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
