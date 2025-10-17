package com.example.demo.product.service;

import com.example.demo.model.Product;
import com.example.demo.product.dto.ProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "brand.id", target = "brandId")
    @Mapping(source = "brand.name", target = "brandName")
    ProductDTO toDto(Product product);
}
