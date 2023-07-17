package com.gwsoft.restaurantAPI.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class GetTopRestaurantsBasedOnRatingByCuisineRequest extends BaseRequest{

    @NotNull(message = "cuisine name should not be null")
    @Pattern(regexp = "^[a-z]+$", message = "Make sure the cuisine is lower case")
    private String cuisine;

    @NotNull(message = "the value cannot be empty")
    @DecimalMin("0")
    @DecimalMax("5")
    private Double  rating;

}
