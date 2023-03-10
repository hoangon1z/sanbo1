package com.bizzan.bitrade.vo;

import com.bizzan.bitrade.entity.ContractTransaction;
import lombok.Data;

@Data
public class ContractTransactionVo extends ContractTransaction {

    private String memberUsername;

    private String memberRealName;

    private String phone;

    private String email;
}
