package com.sinse.productservice.model.product;

import com.sinse.productservice.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;


public interface JpaProductRepository extends JpaRepository<Product, Integer> {
}
