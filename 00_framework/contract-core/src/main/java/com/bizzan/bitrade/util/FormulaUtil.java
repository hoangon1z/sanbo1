package com.bizzan.bitrade.util;

import java.math.BigDecimal;

/**
 * 合约各种公式
 */
public class FormulaUtil {
    /**
     * 计算开仓均价
     *
     * @return 如果除数==0，公式不成立，直接返回0做特殊处理
     */
    public static BigDecimal getAvgOpenPrice(BigDecimal price1, int share1, BigDecimal price2, int share2) {
        if (share1 == 0 || price1.compareTo(BigDecimal.ZERO) == 0) {
            return price2;
        }
        if (share2 == 0 || price2.compareTo(BigDecimal.ZERO) == 0) {
            return price1;
        }
        BigDecimal shareAll = BigDecimal.valueOf(share1 + share2);
        BigDecimal divide1 = BigDecimal.valueOf(share1).divide(price1, 8, BigDecimal.ROUND_DOWN);
        BigDecimal divide2 = BigDecimal.valueOf(share2).divide(price2, 8, BigDecimal.ROUND_DOWN);
        BigDecimal add = divide1.add(divide2);
        if (add.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return shareAll.divide(add, 8, BigDecimal.ROUND_DOWN);
    }
}
