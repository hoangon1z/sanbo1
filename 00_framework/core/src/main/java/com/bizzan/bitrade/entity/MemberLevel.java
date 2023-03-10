package com.bizzan.bitrade.entity;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author GS
 * @description 会员等级实体
 * @date 2017/12/26 17:12
 */
@Data
@Entity
@Table(name = "member_level")
public class MemberLevel {
    @Id
    private Long id;
    @NotBlank(message = "会员等级名称不得为空")
    private String name;
    @NotNull(message = "默认不得为null")
    private Boolean isDefault;
    private String remark;
    // 折扣
    private BigDecimal discount;

    // 条件1
    private String extTerm1;

    // 条件2
    private String extTerm2;

    // 条件3
    private String extTerm3;

    // 条件4
    private String extTerm4;

    // 其它权益1
    private String extRight1;

    // 其它权益2
    private String extRight2;

    // 其它权益3
    private String extRight3;
}
