package com.example.demo.regulatorylabel.service;

import com.example.demo.regulatorylabel.dto.RegulatoryLabelCreateRequest;
import com.example.demo.regulatorylabel.dto.RegulatoryLabelDTO;
import com.example.demo.regulatorylabel.dto.RegulatoryLabelUpdateRequest;

import java.util.List;

public interface RegulatoryLabelService {
    List<RegulatoryLabelDTO> getAllRegulatoryLabels();
    RegulatoryLabelDTO getRegulatoryLabelById(Long id);
    RegulatoryLabelDTO createRegulatoryLabel(RegulatoryLabelCreateRequest request);
    RegulatoryLabelDTO updateRegulatoryLabel(Long id, RegulatoryLabelUpdateRequest request);
    void deleteRegulatoryLabel(Long id);
}
