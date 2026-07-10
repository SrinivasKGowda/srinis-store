package com.shopsphere.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

public class ProductDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotBlank
        private String name;

        private String description;

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        private BigDecimal price;

        @NotBlank
        private String sku;

        @NotNull
        private Long categoryId;

        @NotNull
        @Min(0)
        private Integer initialQuantity;

        private Set<Long> tagIds;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private String sku;
        private String imagePath;
        private Long categoryId;
        private String categoryName;
        private Integer quantity;
        private Set<String> tags;
    }
}
