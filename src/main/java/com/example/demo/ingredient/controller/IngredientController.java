package com.example.demo.ingredient.controller;

import com.example.demo.ingredient.dto.IngredientCreateRequest;
import com.example.demo.ingredient.dto.IngredientDTO;
import com.example.demo.ingredient.dto.IngredientUpdateRequest;
import com.example.demo.ingredient.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    public ResponseEntity<List<IngredientDTO>> getAllIngredients() {
        List<IngredientDTO> ingredients = ingredientService.getAllIngredients();
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientDTO> getIngredientById(@PathVariable Long id) {
        IngredientDTO ingredient = ingredientService.getIngredientById(id);
        return ResponseEntity.ok(ingredient);
    }

    @PostMapping
    public ResponseEntity<IngredientDTO> createIngredient(@RequestBody IngredientCreateRequest request) {
        IngredientDTO createdIngredient = ingredientService.createIngredient(request);
        return new ResponseEntity<>(createdIngredient, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientDTO> updateIngredient(@PathVariable Long id, @RequestBody IngredientUpdateRequest request) {
        IngredientDTO updatedIngredient = ingredientService.updateIngredient(id, request);
        return ResponseEntity.ok(updatedIngredient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
}
