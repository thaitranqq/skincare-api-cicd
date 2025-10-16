package com.example.demo.brand.service;

import com.example.demo.brand.dto.BrandCreateRequest;
import com.example.demo.brand.dto.BrandDTO;
import com.example.demo.brand.dto.BrandUpdateRequest;

import java.util.List;

public interface BrandService {
    List<BrandDTO> getAllBrands();
    BrandDTO getBrandById(Long id);
    BrandDTO createBrand(BrandCreateRequest request);
    BrandDTO updateBrand(Long id, BrandUpdateRequest request);
    void deleteBrand(Long id);
}
