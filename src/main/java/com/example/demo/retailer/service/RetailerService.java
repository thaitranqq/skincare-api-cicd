package com.example.demo.retailer.service;

import com.example.demo.retailer.dto.RetailerCreateRequest;
import com.example.demo.retailer.dto.RetailerDTO;
import com.example.demo.retailer.dto.RetailerUpdateRequest;

import java.util.List;

public interface RetailerService {
    List<RetailerDTO> getAllRetailers();
    RetailerDTO getRetailerById(Long id);
    RetailerDTO createRetailer(RetailerCreateRequest request);
    RetailerDTO updateRetailer(Long id, RetailerUpdateRequest request);
    void deleteRetailer(Long id);
}
