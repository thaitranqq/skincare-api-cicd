package com.example.demo.admin.service.impl;

import com.example.demo.admin.dto.SearchStatsDTO;
import com.example.demo.admin.dto.UserTrendDTO;
import com.example.demo.admin.service.AdminService;
import com.example.demo.product.dto.ProductDTO;
import com.example.demo.product.service.ProductMapper;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final ProductRepository productRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public UserTrendDTO getUserTrends() {
        UserTrendDTO trends = new UserTrendDTO();

        // Trend by skin type (using JdbcTemplate as profiles are not a full entity)
        List<Map<String, Object>> bySkinType = jdbcTemplate.queryForList(
            "SELECT skin_type, COUNT(*) as count FROM profiles WHERE skin_type IS NOT NULL GROUP BY skin_type"
        );
        trends.setBySkinType(bySkinType);

        // Placeholder for age range trends - requires age/birthdate data in the users/profiles table
        trends.setPopularProducts(Collections.emptyList());

        return trends;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getRiskyProductsReport() {
        return productRepository.findRiskyProducts().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SearchStatsDTO getSearchFrequencyStats() {
        // Placeholder logic: This would require logging search queries and barcode scans in an 'events' table or similar
        SearchStatsDTO stats = new SearchStatsDTO();
        stats.setBarcodeScans(Collections.emptyList());
        stats.setKeywordSearches(Collections.emptyList());
        return stats;
    }
}
