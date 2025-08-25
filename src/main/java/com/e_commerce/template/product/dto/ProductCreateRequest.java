package com.e_commerce.template.product.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductCreateRequest {
    private String productId;
    private String title;
    private String description;
    private double price;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getProductId() {
        return productId;
    }
}
