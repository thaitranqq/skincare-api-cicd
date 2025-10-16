package com.example.demo.product.service.impl;

import com.example.demo.model.Brand;
import com.example.demo.model.Product;
import com.example.demo.product.dto.ProductCreateRequest;
import com.example.demo.product.dto.ProductDTO;
import com.example.demo.product.dto.ProductUpdateRequest;
import com.example.demo.product.service.ProductService;
import com.example.demo.repository.BrandRepository;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    @Override
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::toDto);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return toDto(product);
    }

    @Override
    public ProductDTO createProduct(ProductCreateRequest request) {
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found with id: " + request.getBrandId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setUpcEan(request.getUpcEan());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
        product.setCountry(request.getCountry());
        product.setBrand(brand);

        Product savedProduct = productRepository.save(product);
        return toDto(savedProduct);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getUpcEan() != null) {
            product.setUpcEan(request.getUpcEan());
        }
        if (request.getCategory() != null) {
            product.setCategory(request.getCategory());
        }
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }
        if (request.getCountry() != null) {
            product.setCountry(request.getCountry());
        }
        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found with id: " + request.getBrandId()));
            product.setBrand(brand);
        }

        Product updatedProduct = productRepository.save(product);
        return toDto(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductDTO toDto(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setUpcEan(product.getUpcEan());
        dto.setCategory(product.getCategory());
        dto.setImageUrl(product.getImageUrl());
        dto.setCountry(product.getCountry());
        if (product.getBrand() != null) {
            dto.setBrandId(product.getBrand().getId());
            dto.setBrandName(product.getBrand().getName());
        }
        // Removed ingredients mapping to avoid lazy loading issues
        dto.setCreatedAt(product.getCreatedAt());
        return dto;
    }
}
