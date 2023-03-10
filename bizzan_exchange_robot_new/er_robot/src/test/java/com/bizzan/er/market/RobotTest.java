package com.bizzan.er.market;

import com.bizzan.er.normal.entity.RobotQuota;
import com.bizzan.er.normal.utils.MyRandomUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Unit test for simple App.
 */
public class RobotTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public RobotTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( RobotTest.class );
    }


    public class RobotQuota {
        private Long time = 0l; //时间
        private BigDecimal price = new BigDecimal(0); // 价格

        public RobotQuota() {
        }

        public Long getTime() {
            return time;
        }

        public void setTime(Long time) {
            this.time = time;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"time\":" + "\"" + time + "\"" +
                    ", \"price\":" + "\"" + price + "\"" +
                    '}';
        }
    }

    public int getRandom(int min, int max){
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;
    }

    /**
     * 获取阶梯正确价格
     * @param before
     * @param after
     * @param time
     * @return
     */
    public BigDecimal getLadderPirce(RobotQuota before, RobotQuota after, long time) {
        long bt = time - before.getTime();
        long at = after.getTime() - time;
        BigDecimal ladder = BigDecimal.valueOf(bt).divide(BigDecimal.valueOf(bt + at), 8, BigDecimal.ROUND_DOWN);
        BigDecimal scope = after.getPrice().subtract(before.getPrice());
        BigDecimal to = scope.multiply(ladder);
        BigDecimal price = to.add(before.getPrice());
        return price;
    }

    /**
     * 根据某个点位幅度获取随机价格
     * @param price
     * @param points
     * @return
     */
    public BigDecimal getRandPriceLimintPoints(BigDecimal price, BigDecimal points) {
        BigDecimal randomPoints = MyRandomUtil.getRandom(points.negate(), points);
        return price.add(price.multiply(randomPoints));
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws ParseException {
        int level = 2;
        long now = new Date().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(now);
        Date todayDate = sdf.parse(today);
        long todayBegin = todayDate.getTime();
        long time = (now - todayBegin) / 1000 / 60 ;
        time = time * level;
        RobotQuota r1 = null;
        RobotQuota r2 = null;

        if (r1==null) { //构造假数据 根据kick的值，0.5-0.7
            r1 = new RobotQuota();
            r1.setTime(time/level-1);
            r1.setPrice(BigDecimal.valueOf(0.1488));
        }
        if (r2==null) { // 构造假数据
            r2 = new RobotQuota();
            r2.setTime(time/level+60);
            r2.setPrice(BigDecimal.valueOf(0.1488));
        }
        r1.setTime(r1.getTime()*level);
        r2.setTime(r2.getTime()*level);
        List<RobotQuota> quotaLines = new ArrayList<>();
        quotaLines.add(r1);
        int st = r1.getTime().intValue();
        BigDecimal bp = r1.price; // 上一个价格
        while (st < r2.getTime().intValue()) {
            int slice = MyRandomUtil.getRandom(1, 4); // 取1-4的区间段
            st += slice;
            if (st >= r2.getTime().intValue()) {
                quotaLines.add(r2);
            } else {
                BigDecimal cp = getLadderPirce(r1, r2, Long.valueOf(st));
                BigDecimal rp = getRandPriceLimintPoints(cp, BigDecimal.valueOf(0.05));
                cp = (bp.add(cp)).divide(BigDecimal.valueOf(2));
                bp =cp;
                RobotQuota rrq = new RobotQuota();
                rrq.setTime(Long.valueOf(st));
                rrq.setPrice(rp);
                quotaLines.add(rrq);
            }
        }
        System.out.println("控盘数据");
        System.out.println(quotaLines);
    }



    public void testTime() {
        try {
            long now = new Date().getTime();
            System.out.println(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(now);
            System.out.println(today);
            Date todayDate = sdf.parse(today);
            long todayBegin = todayDate.getTime();
            System.out.println(todayBegin);
            long time = (now - todayBegin) / 1000 / 60;
            System.out.println(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void rand() {

    }
}
