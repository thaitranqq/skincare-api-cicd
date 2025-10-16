package com.example.demo.regulatorylabel.service.impl;

import com.example.demo.model.RegulatoryLabel;
import com.example.demo.regulatorylabel.dto.RegulatoryLabelCreateRequest;
import com.example.demo.regulatorylabel.dto.RegulatoryLabelDTO;
import com.example.demo.regulatorylabel.dto.RegulatoryLabelUpdateRequest;
import com.example.demo.regulatorylabel.service.RegulatoryLabelService;
import com.example.demo.repository.RegulatoryLabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegulatoryLabelServiceImpl implements RegulatoryLabelService {

    private final RegulatoryLabelRepository regulatoryLabelRepository;

    @Override
    public List<RegulatoryLabelDTO> getAllRegulatoryLabels() {
        return regulatoryLabelRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RegulatoryLabelDTO getRegulatoryLabelById(Long id) {
        RegulatoryLabel label = regulatoryLabelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Regulatory Label not found with id: " + id));
        return toDto(label);
    }

    @Override
    public RegulatoryLabelDTO createRegulatoryLabel(RegulatoryLabelCreateRequest request) {
        RegulatoryLabel label = new RegulatoryLabel();
        label.setRegion(request.getRegion());
        label.setCode(request.getCode());
        label.setDescription(request.getDescription());
        label.setLevel(request.getLevel());

        RegulatoryLabel savedLabel = regulatoryLabelRepository.save(label);
        return toDto(savedLabel);
    }

    @Override
    public RegulatoryLabelDTO updateRegulatoryLabel(Long id, RegulatoryLabelUpdateRequest request) {
        RegulatoryLabel label = regulatoryLabelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Regulatory Label not found with id: " + id));

        if (request.getRegion() != null) {
            label.setRegion(request.getRegion());
        }
        if (request.getCode() != null) {
            label.setCode(request.getCode());
        }
        if (request.getDescription() != null) {
            label.setDescription(request.getDescription());
        }
        if (request.getLevel() != null) {
            label.setLevel(request.getLevel());
        }

        RegulatoryLabel updatedLabel = regulatoryLabelRepository.save(label);
        return toDto(updatedLabel);
    }

    @Override
    public void deleteRegulatoryLabel(Long id) {
        if (!regulatoryLabelRepository.existsById(id)) {
            throw new RuntimeException("Regulatory Label not found with id: " + id);
        }
        regulatoryLabelRepository.deleteById(id);
    }

    private RegulatoryLabelDTO toDto(RegulatoryLabel label) {
        RegulatoryLabelDTO dto = new RegulatoryLabelDTO();
        dto.setId(label.getId());
        dto.setRegion(label.getRegion());
        dto.setCode(label.getCode());
        dto.setDescription(label.getDescription());
        dto.setLevel(label.getLevel());
        return dto;
    }
}
