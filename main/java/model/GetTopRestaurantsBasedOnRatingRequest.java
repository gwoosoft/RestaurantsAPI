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
public class GetTopRestaurantsBasedOnRatingRequest extends BaseRequest {
    @NotNull(message = "the value cannot be empty")
    @DecimalMin("0")
    @DecimalMax("5")
    private Double  rating;
}
