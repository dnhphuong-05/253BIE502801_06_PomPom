package com.pompom.group6.models;

import androidx.annotation.ColorRes;

/**
 * A single makeup / lipstick shade shown as a swatch in AI Makeup Artist and AR Try-on.
 * Demo data model – color comes from a resource (no hardcoded hex), price is optional.
 */
public class Shade {
    private final String name;
    @ColorRes
    private final int colorRes;
    private final double price;

    public Shade(String name, @ColorRes int colorRes, double price) {
        this.name = name;
        this.colorRes = colorRes;
        this.price = price;
    }

    public String getName() { return name; }

    @ColorRes
    public int getColorRes() { return colorRes; }

    public double getPrice() { return price; }
}
