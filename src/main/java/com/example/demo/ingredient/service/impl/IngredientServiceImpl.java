package com.example.demo.ingredient.service.impl;

import com.example.demo.model.Ingredient;
import com.example.demo.ingredient.dto.IngredientCreateRequest;
import com.example.demo.ingredient.dto.IngredientDTO;
import com.example.demo.ingredient.dto.IngredientUpdateRequest;
import com.example.demo.ingredient.service.IngredientService;
import com.example.demo.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;

    @Override
    public List<IngredientDTO> getAllIngredients() {
        return ingredientRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public IngredientDTO getIngredientById(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + id));
        return toDto(ingredient);
    }

    @Override
    public IngredientDTO createIngredient(IngredientCreateRequest request) {
        Ingredient ingredient = new Ingredient();
        ingredient.setInciName(request.getInciName());
        ingredient.setAliasVi(request.getAliasVi());
        ingredient.setDescriptionVi(request.getDescriptionVi());
        ingredient.setFunctions(request.getFunctions());
        ingredient.setRiskLevel(request.getRiskLevel());
        ingredient.setBannedIn(request.getBannedIn());
        ingredient.setTypicalRange(request.getTypicalRange());
        ingredient.setSources(request.getSources());

        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        return toDto(savedIngredient);
    }

    @Override
    public IngredientDTO updateIngredient(Long id, IngredientUpdateRequest request) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + id));

        if (request.getInciName() != null) {
            ingredient.setInciName(request.getInciName());
        }
        if (request.getAliasVi() != null) {
            ingredient.setAliasVi(request.getAliasVi());
        }
        if (request.getDescriptionVi() != null) {
            ingredient.setDescriptionVi(request.getDescriptionVi());
        }
        if (request.getFunctions() != null) {
            ingredient.setFunctions(request.getFunctions());
        }
        if (request.getRiskLevel() != null) {
            ingredient.setRiskLevel(request.getRiskLevel());
        }
        if (request.getBannedIn() != null) {
            ingredient.setBannedIn(request.getBannedIn());
        }
        if (request.getTypicalRange() != null) {
            ingredient.setTypicalRange(request.getTypicalRange());
        }
        if (request.getSources() != null) {
            ingredient.setSources(request.getSources());
        }

        Ingredient updatedIngredient = ingredientRepository.save(ingredient);
        return toDto(updatedIngredient);
    }

    @Override
    public void deleteIngredient(Long id) {
        if (!ingredientRepository.existsById(id)) {
            throw new RuntimeException("Ingredient not found with id: " + id);
        }
        ingredientRepository.deleteById(id);
    }

    // Simple DTO mapping method.
    private IngredientDTO toDto(Ingredient ingredient) {
        IngredientDTO dto = new IngredientDTO();
        dto.setId(ingredient.getId());
        dto.setInciName(ingredient.getInciName());
        dto.setAliasVi(ingredient.getAliasVi());
        dto.setDescriptionVi(ingredient.getDescriptionVi());
        dto.setFunctions(ingredient.getFunctions());
        dto.setRiskLevel(ingredient.getRiskLevel());
        dto.setBannedIn(ingredient.getBannedIn());
        dto.setTypicalRange(ingredient.getTypicalRange());
        dto.setSources(ingredient.getSources());
        return dto;
    }
}
