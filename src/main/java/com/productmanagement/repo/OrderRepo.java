package com.productmanagement.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.productmanagement.model.Order;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

}
