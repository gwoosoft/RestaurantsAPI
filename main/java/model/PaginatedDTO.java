package com.gwsoft.restaurantAPI.model;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaginatedDTO<T> {
    final Iterable<T> items;
    final String lastTokens;

    public PaginatedDTO(Iterable<T> items, String lastTokens) {
        this.items = items;
        this.lastTokens = lastTokens;
    }

    public Iterable<T> getItems() {
        return items;
    }

    public String getLastTokens() {
        return lastTokens;
    }

}
