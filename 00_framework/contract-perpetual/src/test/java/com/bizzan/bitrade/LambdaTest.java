package com.bizzan.bitrade;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.OptionalDouble;
import java.util.stream.DoubleStream;

public class LambdaTest {

    class Plate {

        public Plate(BigDecimal price) {
            this.price = price;
        }

        private BigDecimal price;

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }

    @Test
    public void max() {
        ArrayList<Plate> plates = new ArrayList<>();
        plates.add(new Plate(BigDecimal.valueOf(0.6)));
        plates.add(new Plate(BigDecimal.valueOf(1.6)));
        plates.add(new Plate(BigDecimal.valueOf(3.6)));
        plates.add(new Plate(BigDecimal.valueOf(2.1)));
        DoubleStream doubleStream = plates.stream().mapToDouble(e -> e.getPrice().doubleValue());
        OptionalDouble optionalDouble = doubleStream.max();
        double max = optionalDouble.getAsDouble();
        System.out.println(max);
    }
}
