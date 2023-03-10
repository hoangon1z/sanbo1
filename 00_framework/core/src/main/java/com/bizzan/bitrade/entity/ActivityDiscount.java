package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Table
public class ActivityDiscount implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "activity_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Activity activity;

    // 折扣
    private BigDecimal discount;

    // 会员等级ID
    private Long memberLevelId;
}
