package com.productmanagement.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.productmanagement.model.Order;
import com.productmanagement.model.ProductLine;

@Repository
public interface ProductLinerepo extends JpaRepository<ProductLine, Long> {

	List<ProductLine> findAllByProduct(Order order);

	List<ProductLine> findAllBy_order(Order order);

}
