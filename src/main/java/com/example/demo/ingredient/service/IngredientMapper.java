package com.example.demo.ingredient.service;

import com.example.demo.ingredient.dto.IngredientCreateRequest;
import com.example.demo.ingredient.dto.IngredientDTO;
import com.example.demo.ingredient.dto.IngredientUpdateRequest;
import com.example.demo.model.Ingredient;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.web.util.HtmlUtils;

@Mapper(componentModel = "spring", imports = HtmlUtils.class)
public interface IngredientMapper {

    IngredientDTO toDto(Ingredient ingredient);

    Ingredient toEntity(IngredientCreateRequest request);

    void updateEntityFromRequest(IngredientUpdateRequest request, @MappingTarget Ingredient ingredient);

    default String escapeHtml(String text) {
        return text != null ? HtmlUtils.htmlEscape(text) : null;
    }

    // Custom mapping for create request with sanitization
    @org.mapstruct.Mapping(target = "inciName", expression = "java(escapeHtml(request.getInciName()))")
    @org.mapstruct.Mapping(target = "aliasVi", expression = "java(escapeHtml(request.getAliasVi()))")
    @org.mapstruct.Mapping(target = "descriptionVi", expression = "java(escapeHtml(request.getDescriptionVi()))")
    @org.mapstruct.Mapping(target = "functions", expression = "java(escapeHtml(request.getFunctions()))")
    @org.mapstruct.Mapping(target = "riskLevel", expression = "java(escapeHtml(request.getRiskLevel()))")
    @org.mapstruct.Mapping(target = "bannedIn", expression = "java(escapeHtml(request.getBannedIn()))")
    @org.mapstruct.Mapping(target = "typicalRange", expression = "java(escapeHtml(request.getTypicalRange()))")
    @org.mapstruct.Mapping(target = "sources", expression = "java(escapeHtml(request.getSources()))")
    Ingredient createRequestToEntity(IngredientCreateRequest request);

    // Custom mapping for update request with sanitization
    @org.mapstruct.Mapping(target = "inciName", expression = "java(escapeHtml(request.getInciName()))")
    @org.mapstruct.Mapping(target = "aliasVi", expression = "java(escapeHtml(request.getAliasVi()))")
    @org.mapstruct.Mapping(target = "descriptionVi", expression = "java(escapeHtml(request.getDescriptionVi()))")
    @org.mapstruct.Mapping(target = "functions", expression = "java(escapeHtml(request.getFunctions()))")
    @org.mapstruct.Mapping(target = "riskLevel", expression = "java(escapeHtml(request.getRiskLevel()))")
    @org.mapstruct.Mapping(target = "bannedIn", expression = "java(escapeHtml(request.getBannedIn()))")
    @org.mapstruct.Mapping(target = "typicalRange", expression = "java(escapeHtml(request.getTypicalRange()))")
    @org.mapstruct.Mapping(target = "sources", expression = "java(escapeHtml(request.getSources()))")
    void updateRequestToEntity(IngredientUpdateRequest request, @MappingTarget Ingredient ingredient);
}
