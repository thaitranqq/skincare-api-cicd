package com.example.demo.rec.service.impl;

import com.example.demo.model.Rec;
import com.example.demo.rec.dto.RecCreateRequest;
import com.example.demo.rec.dto.RecDTO;
import com.example.demo.rec.dto.RecUpdateRequest;
import com.example.demo.rec.service.RecService;
import com.example.demo.repository.RecRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecServiceImpl implements RecService {

    private final RecRepository recRepository;

    @Override
    public List<RecDTO> getAllRecs() {
        return recRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RecDTO getRecById(Long id) {
        Rec rec = recRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recommendation not found with id: " + id));
        return toDto(rec);
    }

    @Override
    public List<RecDTO> getRecsByUserId(Long userId) {
        return recRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecDTO> getRecsByProductId(Long productId) {
        return recRepository.findByProductId(productId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RecDTO createRec(RecCreateRequest request) {
        Rec rec = new Rec();
        rec.setUserId(request.getUserId());
        rec.setProductId(request.getProductId());
        rec.setScore(request.getScore());
        rec.setReasonJson(HtmlUtils.htmlEscape(request.getReasonJson()));
        rec.setCreatedAt(OffsetDateTime.now());

        Rec savedRec = recRepository.save(rec);
        return toDto(savedRec);
    }

    @Override
    public RecDTO updateRec(Long id, RecUpdateRequest request) {
        Rec rec = recRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recommendation not found with id: " + id));

        if (request.getScore() != null) {
            rec.setScore(request.getScore());
        }
        if (request.getReasonJson() != null) {
            rec.setReasonJson(HtmlUtils.htmlEscape(request.getReasonJson()));
        }

        Rec updatedRec = recRepository.save(rec);
        return toDto(updatedRec);
    }

    @Override
    public void deleteRec(Long id) {
        if (!recRepository.existsById(id)) {
            throw new RuntimeException("Recommendation not found with id: " + id);
        }
        recRepository.deleteById(id);
    }

    private RecDTO toDto(Rec rec) {
        RecDTO dto = new RecDTO();
        dto.setId(rec.getId());
        dto.setUserId(rec.getUserId());
        dto.setProductId(rec.getProductId());
        dto.setScore(rec.getScore());
        dto.setReasonJson(rec.getReasonJson());
        dto.setCreatedAt(rec.getCreatedAt());
        return dto;
    }
}
