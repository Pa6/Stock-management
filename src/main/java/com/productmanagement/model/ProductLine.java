package com.productmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "production_lines")
public class ProductLine {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")  // Specify the foreign key column name
    private Product product;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "order_id")  // Specify the foreign key column name
    @JsonIgnore
    private Order _order;
    
    public Order getOrder() {
    	return _order;
    }
    
    public void setOrder(Order order) {
    	this._order = order;
    }

}
