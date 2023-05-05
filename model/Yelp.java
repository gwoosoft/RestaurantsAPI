package com.gwsoft.restaurantAPI.model;

import java.util.ArrayList;

public class Yelp {
    public ArrayList<Business> getBusinesses() {
        return businesses;
    }

    public void setBusinesses(ArrayList<Business> businesses) {
        this.businesses = businesses;
    }

    private ArrayList<Business> businesses;
}