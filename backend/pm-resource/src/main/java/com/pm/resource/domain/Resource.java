package com.pm.resource.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pm.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("resource")
@Schema(description = "资源")
public class Resource extends BaseEntity {

    @Schema(description = "关联用户ID")
    private Long userId;

    @Schema(description = "所属项目ID")
    private Long projectId;

    @Schema(description = "技能标签（逗号分隔）")
    private String skillTags;

    @Schema(description = "可用状态（available/busy/unavailable/on_leave）")
    private String availability;

    @Schema(description = "当前工作负载百分比")
    private Integer workloadPercent;

    @Schema(description = "每周可用工时")
    private BigDecimal capacityHoursPerWeek;

    @Schema(description = "备注")
    private String remark;
}
