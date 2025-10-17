package com.example.demo.ingredient.service.impl;

import com.example.demo.model.Ingredient;
import com.example.demo.ingredient.dto.IngredientCreateRequest;
import com.example.demo.ingredient.dto.IngredientDTO;
import com.example.demo.ingredient.dto.IngredientUpdateRequest;
import com.example.demo.ingredient.service.IngredientService;
import com.example.demo.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

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
        setIngredientProperties(ingredient, request);

        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        return toDto(savedIngredient);
    }

    @Override
    public IngredientDTO updateIngredient(Long id, IngredientUpdateRequest request) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + id));

        setIngredientProperties(ingredient, request);

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

    // Helper method to set properties from request, including XSS sanitization
    private void setIngredientProperties(Ingredient ingredient, IngredientCreateRequest request) {
        ingredient.setInciName(HtmlUtils.htmlEscape(request.getInciName()));
        if (request.getAliasVi() != null) {
            ingredient.setAliasVi(HtmlUtils.htmlEscape(request.getAliasVi()));
        }
        if (request.getDescriptionVi() != null) {
            ingredient.setDescriptionVi(HtmlUtils.htmlEscape(request.getDescriptionVi()));
        }
        if (request.getFunctions() != null) {
            ingredient.setFunctions(HtmlUtils.htmlEscape(request.getFunctions()));
        }
        if (request.getRiskLevel() != null) {
            ingredient.setRiskLevel(HtmlUtils.htmlEscape(request.getRiskLevel()));
        }
        if (request.getBannedIn() != null) {
            ingredient.setBannedIn(HtmlUtils.htmlEscape(request.getBannedIn()));
        }
        if (request.getTypicalRange() != null) {
            ingredient.setTypicalRange(HtmlUtils.htmlEscape(request.getTypicalRange()));
        }
        if (request.getSources() != null) {
            ingredient.setSources(HtmlUtils.htmlEscape(request.getSources()));
        }
    }

    private void setIngredientProperties(Ingredient ingredient, IngredientUpdateRequest request) {
        if (request.getInciName() != null) {
            ingredient.setInciName(HtmlUtils.htmlEscape(request.getInciName()));
        }
        if (request.getAliasVi() != null) {
            ingredient.setAliasVi(HtmlUtils.htmlEscape(request.getAliasVi()));
        }
        if (request.getDescriptionVi() != null) {
            ingredient.setDescriptionVi(HtmlUtils.htmlEscape(request.getDescriptionVi()));
        }
        if (request.getFunctions() != null) {
            ingredient.setFunctions(HtmlUtils.htmlEscape(request.getFunctions()));
        }
        if (request.getRiskLevel() != null) {
            ingredient.setRiskLevel(HtmlUtils.htmlEscape(request.getRiskLevel()));
        }
        if (request.getBannedIn() != null) {
            ingredient.setBannedIn(HtmlUtils.htmlEscape(request.getBannedIn()));
        }
        if (request.getTypicalRange() != null) {
            ingredient.setTypicalRange(HtmlUtils.htmlEscape(request.getTypicalRange()));
        }
        if (request.getSources() != null) {
            ingredient.setSources(HtmlUtils.htmlEscape(request.getSources()));
        }
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
