package com.example.demo.ingredient.service;

import com.example.demo.ingredient.dto.IngredientCreateRequest;
import com.example.demo.ingredient.dto.IngredientDTO;
import com.example.demo.ingredient.dto.IngredientUpdateRequest;

import java.util.List;

public interface IngredientService {
    List<IngredientDTO> getAllIngredients();
    IngredientDTO getIngredientById(Long id);
    IngredientDTO createIngredient(IngredientCreateRequest request);
    IngredientDTO updateIngredient(Long id, IngredientUpdateRequest request);
    void deleteIngredient(Long id);
}
