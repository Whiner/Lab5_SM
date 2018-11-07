package org.lab5;

import java.util.Random;

public class Randomizer {
    public final static double INTERVAL_M = 5;
    public final static double INTERVAL_CKO = 1;
    public final static double WAIT_INTERVAL_M = 10;
    public final static double SERVICE_INTERVAL_M = 30;

    private Random r = new Random();

    private final int n = 10;

    public double getKcu() {
        return r.nextDouble();
    }

    private double getUniformY(double a, double b) {
        return a + (b - a) * getKcu();
    }

    public double getNormalY(double m, double cko) {
        double y = 0;
        for (int i = 0; i < n; i++) {
            double a = m / n - (Math.sqrt(3) * cko) / n;
            double b = m / n + (Math.sqrt(3) * cko) / n;
            double uniformY = getUniformY(a, b);
            y += uniformY;
        }
        return y;
    }


    public double getExpY(double m_Exp) {
        return -m_Exp * Math.log(getKcu());
    }

    public int getN() {
        return n;
    }


}
