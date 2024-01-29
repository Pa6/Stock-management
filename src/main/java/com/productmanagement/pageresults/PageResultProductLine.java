package com.productmanagement.pageresults;

import java.util.List;

import com.productmanagement.model.ProductLine;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageResultProductLine {
    private List<ProductLine> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
