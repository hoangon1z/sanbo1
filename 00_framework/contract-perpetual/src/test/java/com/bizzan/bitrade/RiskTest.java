package com.bizzan.bitrade;

import com.bizzan.bitrade.entity.RiskPeriod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

public class RiskTest {

    @Test
    public void test1() {
//        float fl = (float) Math.random();
//        System.out.println(fl);
        int right = 0;
        int wrong = 0;
        for (int i = 0; i < 100000; i++) {
            if (getAccess(2, 5)) {
                right++;
            } else {
                wrong++;
            }
        }
        System.out.println(right);
        System.out.println(wrong);
    }

    private Boolean getAccess(int share, int minShare) {
        BigDecimal p1 = BigDecimal.ONE.subtract(BigDecimal.valueOf(minShare).divide(BigDecimal.valueOf(share)));
        if (p1.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        BigDecimal p = p1.multiply(BigDecimal.valueOf(1));
        float fl = (float) Math.random();
        return BigDecimal.valueOf(fl).compareTo(p) < 0;
    }
}
