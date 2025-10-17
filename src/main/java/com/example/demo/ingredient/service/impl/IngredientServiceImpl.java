package com.example.demo.ingredient.service.impl;

import com.example.demo.model.Ingredient;
import com.example.demo.ingredient.dto.IngredientCreateRequest;
import com.example.demo.ingredient.dto.IngredientDTO;
import com.example.demo.ingredient.dto.IngredientUpdateRequest;
import com.example.demo.ingredient.service.IngredientService;
import com.example.demo.ingredient.service.IngredientMapper;
import com.example.demo.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    @Override
    public List<IngredientDTO> getAllIngredients() {
        return ingredientRepository.findAll().stream()
                .map(ingredientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public IngredientDTO getIngredientById(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + id));
        return ingredientMapper.toDto(ingredient);
    }

    @Override
    public IngredientDTO createIngredient(IngredientCreateRequest request) {
        Ingredient ingredient = ingredientMapper.createRequestToEntity(request);
        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        return ingredientMapper.toDto(savedIngredient);
    }

    @Override
    public IngredientDTO updateIngredient(Long id, IngredientUpdateRequest request) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + id));

        ingredientMapper.updateRequestToEntity(request, ingredient);

        Ingredient updatedIngredient = ingredientRepository.save(ingredient);
        return ingredientMapper.toDto(updatedIngredient);
    }

    @Override
    public void deleteIngredient(Long id) {
        if (!ingredientRepository.existsById(id)) {
            throw new RuntimeException("Ingredient not found with id: " + id);
        }
        ingredientRepository.deleteById(id);
    }
}
