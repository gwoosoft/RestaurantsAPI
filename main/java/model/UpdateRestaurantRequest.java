package com.gwsoft.restaurantAPI.model;

import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class UpdateRestaurantRequest {

    @NotNull(message = "the value cannot be empty")
    @DecimalMin("0")
    @DecimalMax("5")
    Double customRating;

    @NotNull(message = "userId should not be null")
    @Pattern(regexp = "^[a-zA-Z\\d]+$", message = "Make sure you do not use weird ass symbols")
    String userId;
}
