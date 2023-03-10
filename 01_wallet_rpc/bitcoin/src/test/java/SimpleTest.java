import com.bizzan.bc.wallet.entity.Deposit;
import com.bizzan.bc.wallet.service.DepositService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;

public class SimpleTest {

    @Test
    public void testBigdecimal(){
        BigDecimal a  = new BigDecimal("12.5");
        System.out.println(a);
        a.subtract(BigDecimal.ONE);
        System.out.println(a);
    }

    @Test
    public void deposite(){
        BigDecimal num = new BigDecimal(String.valueOf(0.001500025));
        System.out.println(num);
//        depositService.save(deposit);
    }
}
