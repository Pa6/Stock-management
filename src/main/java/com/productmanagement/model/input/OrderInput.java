package com.productmanagement.model.input;

import java.util.List;

import lombok.Data;

@Data
public class OrderInput {
    private List<Long> productLineIds;
}
