package com.gwsoft.restaurantAPI.model;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaginatedDTO {
    final Iterable<RestaurantDTO> items;
    final String lastTokens;

    public PaginatedDTO(Iterable<RestaurantDTO> items, String lastTokens) {
        this.items = items;
        this.lastTokens = lastTokens;
    }

    public Iterable<RestaurantDTO> getItems() {
        return items;
    }

    public String getLastTokens() {
        return lastTokens;
    }

}
