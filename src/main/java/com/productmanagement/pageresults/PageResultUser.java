package com.productmanagement.pageresults;

import java.util.List;

import com.productmanagement.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageResultUser {
    private List<User> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
