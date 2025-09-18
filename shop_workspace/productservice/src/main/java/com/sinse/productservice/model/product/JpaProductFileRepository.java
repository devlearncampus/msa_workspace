package com.sinse.productservice.model.product;

import com.sinse.productservice.domain.ProductFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductFileRepository extends JpaRepository<ProductFile, Integer> {
}
