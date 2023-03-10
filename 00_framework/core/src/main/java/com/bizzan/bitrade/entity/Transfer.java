package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.TransferDirection;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author GS
 * @date 2018年02月27日
 */
@Entity
@Data
public class Transfer {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private BigDecimal amount;

    private Long memberId;
    @JoinColumn(name = "coin_id")
    @ManyToOne
    private Coin coin;
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @Column(columnDefinition = "decimal(18,8) default 0 comment '手续费'")
    private BigDecimal fee;

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int(4) default 1 comment '划转方向'")
    private TransferDirection direction;
}
