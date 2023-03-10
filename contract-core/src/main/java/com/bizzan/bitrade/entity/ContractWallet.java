package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.BooleanEnum;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@Table(uniqueConstraints ={@UniqueConstraint(columnNames={"memberId", "coin_id"})})
public class ContractWallet {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "coin_id")
    private Coin coin;

    /**
     * 可用余额
     */
    @Column(columnDefinition = "decimal(26,16) comment '可用余额'")
    private BigDecimal balance;

    /**
     * 冻结余额
     */
    @Column(columnDefinition = "decimal(26,16) comment '冻结余额'")
    private BigDecimal frozenBalance;

    /**
     * 钱包是否锁定，0否，1是。锁定后
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int default 0 comment '钱包不是锁定'")
    private BooleanEnum isLock = BooleanEnum.IS_FALSE;
}
