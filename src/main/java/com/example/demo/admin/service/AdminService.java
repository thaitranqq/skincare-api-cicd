package com.example.demo.admin.service;

import com.example.demo.admin.dto.SearchStatsDTO;
import com.example.demo.admin.dto.UserTrendDTO;
import com.example.demo.product.dto.ProductDTO;

import java.util.List;

public interface AdminService {

    UserTrendDTO getUserTrends();

    List<ProductDTO> getRiskyProductsReport();

    SearchStatsDTO getSearchFrequencyStats();

    // Add methods for Q&A and Suggestions later
}
