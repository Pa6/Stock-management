package com.productmanagement.pageresults;

import java.util.List;

import com.productmanagement.model.Order;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageResultOrder {
    private List<Order> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
