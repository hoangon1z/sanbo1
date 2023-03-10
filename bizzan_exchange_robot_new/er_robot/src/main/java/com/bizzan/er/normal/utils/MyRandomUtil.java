package com.bizzan.er.normal.utils;

import java.math.BigDecimal;
import java.util.Random;

public class MyRandomUtil {

    public static BigDecimal getRandom(BigDecimal max, BigDecimal min) {
        Random random = new Random();
        Double nMax = max.doubleValue();
        Double nMin = min.doubleValue();
        Double nRange = nMax-nMin;
        Double nRandomDouble = random.nextDouble()*nRange;
        Double nYouWant = nMax - nRandomDouble;
        return BigDecimal.valueOf(nYouWant);
    }

    public static int getRandom(int min, int max){
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;
    }

}
