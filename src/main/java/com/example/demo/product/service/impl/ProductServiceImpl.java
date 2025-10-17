package com.example.demo.product.service.impl;

import com.example.demo.model.Brand;
import com.example.demo.model.Product;
import com.example.demo.product.dto.ProductCreateRequest;
import com.example.demo.product.dto.ProductDTO;
import com.example.demo.product.dto.ProductUpdateRequest;
import com.example.demo.product.service.ProductService;
import com.example.demo.repository.BrandRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final FileStorageService fileStorageService;

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
        product.setName(HtmlUtils.htmlEscape(request.getName()));
        if (request.getUpcEan() != null) {
            product.setUpcEan(HtmlUtils.htmlEscape(request.getUpcEan()));
        }
        if (request.getCategory() != null) {
            product.setCategory(HtmlUtils.htmlEscape(request.getCategory()));
        }
        if (request.getCountry() != null) {
            product.setCountry(HtmlUtils.htmlEscape(request.getCountry()));
        }
        product.setBrand(brand);

        MultipartFile image = request.getImage();
        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = fileStorageService.save(image);
                product.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }

        Product savedProduct = productRepository.save(product);
        return toDto(savedProduct);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (request.getName() != null) {
            product.setName(HtmlUtils.htmlEscape(request.getName()));
        }
        if (request.getUpcEan() != null) {
            product.setUpcEan(HtmlUtils.htmlEscape(request.getUpcEan()));
        }
        if (request.getCategory() != null) {
            product.setCategory(HtmlUtils.htmlEscape(request.getCategory()));
        }
        if (request.getCountry() != null) {
            product.setCountry(HtmlUtils.htmlEscape(request.getCountry()));
        }
        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found with id: " + request.getBrandId()));
            product.setBrand(brand);
        }

        // Handle image update
        MultipartFile newImage = request.getImage();
        boolean deleteExistingImage = request.isDeleteImage();

        if (deleteExistingImage) {
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                fileStorageService.delete(product.getImageUrl());
                product.setImageUrl(null);
            }
        } else if (newImage != null && !newImage.isEmpty()) {
            try {
                // Delete old image if exists
                if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                    fileStorageService.delete(product.getImageUrl());
                }
                // Upload new image
                String newImageUrl = fileStorageService.save(newImage);
                product.setImageUrl(newImageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload new image", e);
            }
        }

        Product updatedProduct = productRepository.save(product);
        return toDto(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        // Delete image from Azure Blob Storage if it exists
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            fileStorageService.delete(product.getImageUrl());
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
