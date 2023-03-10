package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Entity
@Getter
@Setter
public class RiskStrategy implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @NotNull(message = "策略名不能为空")
    private String name;

    private int diffCloseTime = 5; // 距离平仓时间(秒)

    @Column(columnDefinition = "decimal(7,6) DEFAULT 0.01 comment '距离开仓点位最小'")
    private BigDecimal diffOpenPointMin = BigDecimal.valueOf(0.01); // 距离开仓点位最小

    @Column(columnDefinition = "decimal(7,6) DEFAULT 0.10 comment '距离开仓点位最大'")
    private BigDecimal diffOpenPointMax = BigDecimal.valueOf(0.10); // 距离开仓点位最大

    @Column(columnDefinition = "decimal(7,6) DEFAULT 0 comment '距离平仓点位'")
    private BigDecimal diffClosePoint = BigDecimal.valueOf(0); // 距离平仓点位

    @Column(columnDefinition = "decimal(18,8) DEFAULT 0 comment '收益权重'")
    private BigDecimal profitWeight = BigDecimal.valueOf(0); // 收益权重;

    private Boolean autoClosePoint = false; // 智能结合当前价格选择点位平仓，避免出现过于明显的插针现象

    private int minShare = 1; // 最小平仓手数

    private int resumeTime = 5; // 恢复报价时间(秒)

    @OneToMany(mappedBy = "riskStrategy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RiskStrategyMember> members;

    @Column(columnDefinition = "int(4) default 0 comment '策略状态'")
    private Integer status; // 0 停用，1 起用

    private Boolean isDefault = false; // 默认策略是未参加任何自定义策略的的用户所选策略

    private Boolean profitAutoClose = true; // 盈利关闭

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Long updateTime;//修改时间

    @Override
    public String toString() {
        return "RiskStrategy{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", diffCloseTime=" + diffCloseTime +
                ", diffOpenPointMin=" + diffOpenPointMin +
                ", diffOpenPointMax=" + diffOpenPointMax +
                ", diffClosePoint=" + diffClosePoint +
                ", autoClosePoint=" + autoClosePoint +
                ", minShare=" + minShare +
                ", resumeTime=" + resumeTime +
                ", status=" + status +
                ", isDefault=" + isDefault +
                ", updateTime=" + updateTime +
                '}';
    }
}
