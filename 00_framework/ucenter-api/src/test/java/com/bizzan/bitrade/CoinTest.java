package com.bizzan.bitrade;

import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.MemberWalletService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Member;
import java.math.BigDecimal;

@RunWith(SpringRunner.class)
//启动Spring
@SpringBootTest
public class CoinTest {
    @Autowired
    private MemberService memberService;

    @Test
    public void test1() {
        memberService.addKickFeeHandler(1l, BigDecimal.valueOf(100));
    }
}
