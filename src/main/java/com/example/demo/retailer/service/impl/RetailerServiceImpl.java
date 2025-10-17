package com.example.demo.retailer.service.impl;

import com.example.demo.model.Retailer;
import com.example.demo.retailer.dto.RetailerCreateRequest;
import com.example.demo.retailer.dto.RetailerDTO;
import com.example.demo.retailer.dto.RetailerUpdateRequest;
import com.example.demo.retailer.service.RetailerService;
import com.example.demo.repository.RetailerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetailerServiceImpl implements RetailerService {

    private final RetailerRepository retailerRepository;

    @Override
    public List<RetailerDTO> getAllRetailers() {
        return retailerRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RetailerDTO getRetailerById(Long id) {
        Retailer retailer = retailerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Retailer not found with id: " + id));
        return toDto(retailer);
    }

    @Override
    public RetailerDTO createRetailer(RetailerCreateRequest request) {
        Retailer retailer = new Retailer();
        retailer.setName(HtmlUtils.htmlEscape(request.getName()));
        retailer.setDomain(HtmlUtils.htmlEscape(request.getDomain()));

        Retailer savedRetailer = retailerRepository.save(retailer);
        return toDto(savedRetailer);
    }

    @Override
    public RetailerDTO updateRetailer(Long id, RetailerUpdateRequest request) {
        Retailer retailer = retailerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Retailer not found with id: " + id));

        if (request.getName() != null) {
            retailer.setName(HtmlUtils.htmlEscape(request.getName()));
        }
        if (request.getDomain() != null) {
            retailer.setDomain(HtmlUtils.htmlEscape(request.getDomain()));
        }

        Retailer updatedRetailer = retailerRepository.save(retailer);
        return toDto(updatedRetailer);
    }

    @Override
    public void deleteRetailer(Long id) {
        if (!retailerRepository.existsById(id)) {
            throw new RuntimeException("Retailer not found with id: " + id);
        }
        retailerRepository.deleteById(id);
    }

    private RetailerDTO toDto(Retailer retailer) {
        RetailerDTO dto = new RetailerDTO();
        dto.setId(retailer.getId());
        dto.setName(retailer.getName());
        dto.setDomain(retailer.getDomain());
        return dto;
    }
}
