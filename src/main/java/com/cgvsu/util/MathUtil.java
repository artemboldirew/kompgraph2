package com.cgvsu.util;

import java.util.Arrays;

public class MathUtil {
    static double[] data = new double[1000];
    static {
        Arrays.fill(data, -1);
        data[0] = 1;
        data[1] = 1;
        for (int i = 2; i < 100; i++) {
            data[i] = data[i-1] * i;
        }
    }
    public static double factorial(int n) {
        if (n < 100) {
            return data[n];
        } else {
            double res = data[99];
            for (int i = 100; i <= n; i++) {
                res *= i;
            }
            return res;
        }
    }
}
