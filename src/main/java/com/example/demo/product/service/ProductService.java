package com.example.demo.product.service;

import com.example.demo.product.dto.ProductCreateRequest;
import com.example.demo.product.dto.ProductDTO;
import com.example.demo.product.dto.ProductUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    Page<ProductDTO> getAllProducts(Pageable pageable);
    ProductDTO getProductById(Long id);
    ProductDTO createProduct(ProductCreateRequest request);
    ProductDTO updateProduct(Long id, ProductUpdateRequest request);
    void deleteProduct(Long id);
}
