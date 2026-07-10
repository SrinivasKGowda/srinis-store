package com.shopsphere.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotNull
        private Long customerId;

        @NotEmpty
        @Valid
        private List<OrderItemRequest> items;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class OrderItemRequest {

            @NotNull
            private Long productId;

            @NotNull
            @Min(1)
            private Integer quantity;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private Long id;
        private Long customerId;
        private String customerName;
        private String status;
        private LocalDateTime orderDate;
        private BigDecimal totalAmount;
        private List<OrderItemResponse> items;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class OrderItemResponse {
            private Long productId;
            private String productName;
            private Integer quantity;
            private BigDecimal price;
        }
    }
}
