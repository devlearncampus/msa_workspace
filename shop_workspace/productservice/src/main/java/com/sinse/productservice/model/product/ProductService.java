package com.sinse.productservice.model.product;

import com.sinse.productservice.domain.Product;
import com.sinse.productservice.domain.ProductFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    public void save(Product product, List<MultipartFile> files);
    public List<Product> selectAll();
    public Product select(int productId);
    public void update(Product product, List<MultipartFile> files);
    public void delete(int productId);
}
