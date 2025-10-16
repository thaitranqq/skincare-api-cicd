package com.example.demo.rec.service;

import com.example.demo.rec.dto.RecCreateRequest;
import com.example.demo.rec.dto.RecDTO;
import com.example.demo.rec.dto.RecUpdateRequest;

import java.util.List;

public interface RecService {
    List<RecDTO> getAllRecs();
    RecDTO getRecById(Long id);
    List<RecDTO> getRecsByUserId(Long userId);
    List<RecDTO> getRecsByProductId(Long productId);
    RecDTO createRec(RecCreateRequest request);
    RecDTO updateRec(Long id, RecUpdateRequest request);
    void deleteRec(Long id);
}
