package com.example.demo.brand.service.impl;

import com.example.demo.model.Brand;
import com.example.demo.brand.dto.BrandCreateRequest;
import com.example.demo.brand.dto.BrandDTO;
import com.example.demo.brand.dto.BrandUpdateRequest;
import com.example.demo.brand.service.BrandService;
import com.example.demo.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    public List<BrandDTO> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BrandDTO getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found with id: " + id));
        return toDto(brand);
    }

    @Override
    public BrandDTO createBrand(BrandCreateRequest request) {
        Brand brand = new Brand();
        brand.setName(request.getName());

        Brand savedBrand = brandRepository.save(brand);
        return toDto(savedBrand);
    }

    @Override
    public BrandDTO updateBrand(Long id, BrandUpdateRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found with id: " + id));

        if (request.getName() != null) {
            brand.setName(request.getName());
        }

        Brand updatedBrand = brandRepository.save(brand);
        return toDto(updatedBrand);
    }

    @Override
    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new RuntimeException("Brand not found with id: " + id);
        }
        brandRepository.deleteById(id);
    }

    private BrandDTO toDto(Brand brand) {
        BrandDTO dto = new BrandDTO();
        dto.setId(brand.getId());
        dto.setName(brand.getName());
        return dto;
    }
}
