package com.bizzan.bc.wallet;

import com.bizzan.bc.wallet.entity.Deposit;
import com.bizzan.bc.wallet.service.DepositService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SimpleTest {
    @Autowired
    private DepositService depositService;

    @Test
    public void testBigdecimal(){
        BigDecimal a  = new BigDecimal("12.5");
        System.out.println(a);
        a.subtract(BigDecimal.ONE);
        System.out.println(a);
    }

    @Test
    public void deposite(){
        Deposit deposit = new Deposit();
        deposit.setAddress("16U24t3WjYswD4QW1Xxajg4og2tH8bC4eq");
        deposit.setAmount(new BigDecimal("0.001500000000000000031225022567582"));
        deposit.setBlockHash("A");
        deposit.setBlockHeight(1l);
        deposit.setStatus(0);
        deposit.setTime(new Date());
        deposit.setTxid("A");
        depositService.save(deposit);
    }

}
