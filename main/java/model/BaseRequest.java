package com.gwsoft.restaurantAPI.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Pattern;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseRequest {

    @Digits(message = "please request only digits between 0 and 999", integer = 3, fraction = 0)
    private Integer maxNum;
    private String lastEvaluatedKey;
}
