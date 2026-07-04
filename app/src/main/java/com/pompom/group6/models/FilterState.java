package com.pompom.group6.models;

import java.util.HashSet;
import java.util.Set;

public class FilterState {
    public Set<Integer> categoryIds = new HashSet<>();
    public long minPrice = 0;
    public long maxPrice = 0; // 0 = no max
    public float minRating = 0f;

    // Sort & toggle fields (added for filter refactor)
    public String sortAlpha  = "";   // "" | "asc" | "desc"
    public String sortPrice  = "";   // "" | "asc" | "desc"
    public boolean sortNewest  = false;
    public boolean sortPopular = false;

    public boolean hasActiveFilters() {
        return !categoryIds.isEmpty()
                || minPrice > 0
                || maxPrice > 0
                || minRating > 0
                || !sortAlpha.isEmpty()
                || !sortPrice.isEmpty()
                || sortNewest
                || sortPopular;
    }

    public FilterState copy() {
        FilterState c = new FilterState();
        c.categoryIds  = new HashSet<>(categoryIds);
        c.minPrice     = minPrice;
        c.maxPrice     = maxPrice;
        c.minRating    = minRating;
        c.sortAlpha    = sortAlpha;
        c.sortPrice    = sortPrice;
        c.sortNewest   = sortNewest;
        c.sortPopular  = sortPopular;
        return c;
    }

    public void reset() {
        categoryIds.clear();
        minPrice    = 0;
        maxPrice    = 0;
        minRating   = 0f;
        sortAlpha   = "";
        sortPrice   = "";
        sortNewest  = false;
        sortPopular = false;
    }
}
