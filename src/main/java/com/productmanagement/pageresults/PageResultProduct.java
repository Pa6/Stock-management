package com.productmanagement.pageresults;

import java.util.List;

import com.productmanagement.model.Product;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageResultProduct {
    private List<Product> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
