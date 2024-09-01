package dfomenko.utils;

import org.springframework.stereotype.*;

@Component("calculator")
public class Calculator {
    private Double sum = 0D;

    public Double addSum(Double sumPart) {
        sum += sumPart;
        return sumPart;
    }

    public Double startSum(Double sumPart) {
        sum = sumPart;
        return sumPart;
    }

    public Double stopSum() {
        Double s = sum;
        sum = 0D;
        return s;
    }

    public Double resetSum() {
        sum = 0D;
        return sum;
    }

    public Double getSum() {
        return sum;
    }


}