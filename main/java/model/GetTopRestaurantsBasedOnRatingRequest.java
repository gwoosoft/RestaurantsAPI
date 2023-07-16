package com.gwsoft.restaurantAPI.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTopRestaurantsBasedOnRatingRequest extends BaseRequest {
    @Pattern(regexp = "^[0-9]*$", message = "Please only insert digits")
    @NotNull(message = "rating should not be null")
    @Min(0)
    @Max(5)
    private String rating;
}
