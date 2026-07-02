package com.pompom.group6.models;

import java.util.HashSet;
import java.util.Set;

public class FilterState {
    public Set<Integer> categoryIds = new HashSet<>();
    public long minPrice = 0;
    public long maxPrice = 0; // 0 = no max
    public float minRating = 0f;

    public boolean hasActiveFilters() {
        return !categoryIds.isEmpty() || minPrice > 0 || maxPrice > 0 || minRating > 0;
    }

    public FilterState copy() {
        FilterState c = new FilterState();
        c.categoryIds = new HashSet<>(categoryIds);
        c.minPrice = minPrice;
        c.maxPrice = maxPrice;
        c.minRating = minRating;
        return c;
    }

    public void reset() {
        categoryIds.clear();
        minPrice = 0;
        maxPrice = 0;
        minRating = 0f;
    }
}
