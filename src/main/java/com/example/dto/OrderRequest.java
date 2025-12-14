package com.example.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderRequest {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Item {

        @NotNull
        private Long productId;

        @NotNull
        @Min(1)
        private Integer quantity;
    }

    private List<Item> items;
}
