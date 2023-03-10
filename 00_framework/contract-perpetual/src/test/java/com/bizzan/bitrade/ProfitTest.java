package com.bizzan.bitrade;

import org.junit.Test;

import java.math.BigDecimal;

public class ProfitTest {

    @Test
    public void p() {
        BigDecimal p1 = BigDecimal.ONE.subtract(BigDecimal.valueOf(700).divide(BigDecimal.valueOf(1500), 8, BigDecimal.ROUND_DOWN));
        System.out.println(p1);
    }
}

