package com.e_commerce.template.product.dto;

public class ProductCreateRequest {
    private String title;
    private String description;
    private int price;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }
}
