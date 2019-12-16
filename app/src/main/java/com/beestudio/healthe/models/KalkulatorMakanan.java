package com.beestudio.healthe.models;

public class KalkulatorMakanan {

    public double standarAsupan(double totalTerpenuhi, double totalKebutuhan) {
        double result =  (totalTerpenuhi / totalKebutuhan) * 100;

        return result;
    }

}
