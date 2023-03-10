package com.bizzan.bitrade;

import com.bizzan.bitrade.entity.TradePlateItem;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PriceTest {
    @Test
    public void price() {
        ArrayList<TradePlateItem> lists = new ArrayList<>();
        TradePlateItem tradePlateItem = new TradePlateItem();
        tradePlateItem.setPrice(BigDecimal.valueOf(15.27));
        lists.add(tradePlateItem);
        TradePlateItem tradePlateItem2 = new TradePlateItem();
        tradePlateItem2.setPrice(BigDecimal.valueOf(15.48));
        lists.add(tradePlateItem2);
        TradePlateItem tradePlateItem3 = new TradePlateItem();
        tradePlateItem3.setPrice(BigDecimal.valueOf(15.61));
        lists.add(tradePlateItem3);
        TradePlateItem tradePlateItem4 = new TradePlateItem();
        tradePlateItem4.setPrice(BigDecimal.valueOf(15.89));
        lists.add(tradePlateItem4);
        BigDecimal price = BigDecimal.valueOf(18.45);
        BigDecimal[] ps = splitDecimal(price);
        swithSell(lists, price);
        System.out.println(lists);
    }

    private void swithSell(List<TradePlateItem> sellPlateItems, BigDecimal currentPrice) {
        BigDecimal[] currentPrices = splitDecimal(currentPrice);
        for (TradePlateItem sellPlateItem : sellPlateItems) {
            BigDecimal[] sellPrices = splitDecimal(sellPlateItem.getPrice());
            sellPlateItem.setPrice(currentPrices[0].add(sellPrices[1]));
        }
    }

    private BigDecimal[] splitDecimal(BigDecimal value) {
        BigDecimal[] ps = new BigDecimal[2];
        BigDecimal p1 = value.setScale(0, BigDecimal.ROUND_DOWN);
        BigDecimal p2 = value.subtract(p1);
        ps[0] = p1;
        ps[1] = p2;
        return ps;
    }

    @Test
    public void test2() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            int index = random.nextInt(3);
            System.out.println(index);
        }
    }
}
