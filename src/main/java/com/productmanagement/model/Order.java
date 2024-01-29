package com.productmanagement.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Changed to Long type for consistency

    @OneToMany(mappedBy = "_order", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<ProductLine> productLines;

    @ManyToOne
    @JoinColumn(name = "user_id")  // Specify the foreign key column name
    private User user;
}
