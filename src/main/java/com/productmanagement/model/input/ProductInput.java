package com.productmanagement.model.input;

import lombok.Data;

@Data
public class ProductInput {
	private String name;
    private int stock;
    private double price;
}
