package com.example.demo.routine.service.impl;

import com.example.demo.model.Product;
import com.example.demo.model.Routine;
import com.example.demo.model.RoutineItem;
import com.example.demo.model.RoutineItemId;
import com.example.demo.routine.dto.RoutineCreateRequest;
import com.example.demo.routine.dto.RoutineDTO;
import com.example.demo.routine.dto.RoutineItemCreateRequest;
import com.example.demo.routine.dto.RoutineItemDTO;
import com.example.demo.routine.dto.RoutineItemUpdateRequest;
import com.example.demo.routine.dto.RoutineUpdateRequest;
import com.example.demo.routine.service.RoutineService;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.RoutineItemRepository;
import com.example.demo.repository.RoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional; // Added import

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineServiceImpl implements RoutineService {

    private final RoutineRepository routineRepository;
    private final RoutineItemRepository routineItemRepository;
    private final ProductRepository productRepository;

    // --- Routine operations ---

    @Override
    @Transactional // Added @Transactional
    public List<RoutineDTO> getAllRoutines() {
        return routineRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional // Added @Transactional
    public RoutineDTO getRoutineById(Long id) {
        Routine routine = routineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Routine not found with id: " + id));
        return toDto(routine);
    }

    @Override
    public List<RoutineDTO> getRoutinesByUserId(Long userId) {
        return routineRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RoutineDTO createRoutine(RoutineCreateRequest request) {
        Routine routine = new Routine();
        routine.setUserId(request.getUserId());
        routine.setTitle(request.getTitle());

        Routine savedRoutine = routineRepository.save(routine);
        return toDto(savedRoutine);
    }

    @Override
    public RoutineDTO updateRoutine(Long id, RoutineUpdateRequest request) {
        Routine routine = routineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Routine not found with id: " + id));

        if (request.getTitle() != null) {
            routine.setTitle(request.getTitle());
        }

        Routine updatedRoutine = routineRepository.save(routine);
        return toDto(updatedRoutine);
    }

    @Override
    public void deleteRoutine(Long id) {
        if (!routineRepository.existsById(id)) {
            throw new RuntimeException("Routine not found with id: " + id);
        }
        routineRepository.deleteById(id);
    }

    // --- Routine Item operations ---

    @Override
    public List<RoutineItemDTO> getRoutineItemsByRoutineId(Long routineId) {
        return routineItemRepository.findByRoutineId(routineId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RoutineItemDTO addRoutineItem(RoutineItemCreateRequest request) {
        Routine routine = routineRepository.findById(request.getRoutineId())
                .orElseThrow(() -> new RuntimeException("Routine not found with id: " + request.getRoutineId()));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        RoutineItemId routineItemId = new RoutineItemId(request.getRoutineId(), request.getProductId());
        RoutineItem routineItem = new RoutineItem();
        routineItem.setId(routineItemId);
        routineItem.setRoutine(routine);
        routineItem.setProduct(product);
        routineItem.setStep(request.getStep());
        routineItem.setTimeOfDay(request.getTimeOfDay());

        RoutineItem savedRoutineItem = routineItemRepository.save(routineItem);
        return toDto(savedRoutineItem);
    }

    @Override
    public RoutineItemDTO updateRoutineItem(Long routineId, Long productId, RoutineItemUpdateRequest request) {
        RoutineItemId routineItemId = new RoutineItemId(routineId, productId);
        RoutineItem routineItem = routineItemRepository.findById(routineItemId)
                .orElseThrow(() -> new RuntimeException("Routine Item not found for routineId: " + routineId + " and productId: " + productId));

        if (request.getStep() != null) {
            routineItem.setStep(request.getStep());
        }
        if (request.getTimeOfDay() != null) {
            routineItem.setTimeOfDay(request.getTimeOfDay());
        }

        RoutineItem updatedRoutineItem = routineItemRepository.save(routineItem);
        return toDto(updatedRoutineItem);
    }

    @Override
    public void deleteRoutineItem(Long routineId, Long productId) {
        RoutineItemId routineItemId = new RoutineItemId(routineId, productId);
        if (!routineItemRepository.existsById(routineItemId)) {
            throw new RuntimeException("Routine Item not found for routineId: " + routineId + " and productId: " + productId);
        }
        routineItemRepository.deleteById(routineItemId);
    }

    // --- DTO mapping methods ---

    private RoutineDTO toDto(Routine routine) {
        RoutineDTO dto = new RoutineDTO();
        dto.setId(routine.getId());
        dto.setUserId(routine.getUserId());
        dto.setTitle(routine.getTitle());
        if (routine.getItems() != null) {
            dto.setItems(routine.getItems().stream().map(this::toDto).collect(Collectors.toList()));
        }
        return dto;
    }

    private RoutineItemDTO toDto(RoutineItem routineItem) {
        RoutineItemDTO dto = new RoutineItemDTO();
        dto.setRoutineId(routineItem.getRoutine() != null ? routineItem.getRoutine().getId() : null);
        dto.setProductId(routineItem.getProduct() != null ? routineItem.getProduct().getId() : null);
        dto.setStep(routineItem.getStep());
        dto.setTimeOfDay(routineItem.getTimeOfDay());
        return dto;
    }
}
