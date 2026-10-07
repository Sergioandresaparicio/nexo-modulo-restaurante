package com.roles.usermanagement.modules.sale;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record SaleRequest(
        @NotNull
        @Positive
        Long customerId,

        @NotEmpty
        @Size(max = 100)
        List<@NotNull @Valid Item> items
) {
 public record Item(
         @NotNull
         @Positive
         Long productId,

         @NotNull
         @Min(1)
         @Max(1000000)
         Integer quantity
 ) {}
}