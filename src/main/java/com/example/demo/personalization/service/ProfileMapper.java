package com.example.demo.personalization.service;

import com.example.demo.personalization.dto.ProfileDTO;
import com.example.demo.personalization.model.SkinType;
import com.example.demo.model.Profile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class ProfileMapper {

    private static final Logger logger = LoggerFactory.getLogger(ProfileMapper.class);
    private final ObjectMapper om = new ObjectMapper();

    public ProfileDTO toDto(Profile p) {
        if (p == null) return null;
        ProfileDTO d = new ProfileDTO();
        d.setUserId(p.getUserId());
        // Handle SkinType conversion from String to Enum safely
        try {
            d.setSkinType(p.getSkinType() != null ? SkinType.valueOf(p.getSkinType()) : null);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid SkinType value in DB for userId {}: {}. Setting to null.", p.getUserId(), p.getSkinType());
            d.setSkinType(null);
        }

        try {
            List<String> concerns = p.getConcerns() == null ? Collections.emptyList()
                    : om.readValue(p.getConcerns(), new TypeReference<List<String>>() {});
            d.setConcerns(concerns);
        } catch (Exception e) {
            logger.error("Error parsing concerns JSON for userId {}: {}", p.getUserId(), e.getMessage());
            d.setConcerns(Collections.emptyList());
        }
        try {
            List<String> allergies = p.getAllergies() == null ? Collections.emptyList()
                    : om.readValue(p.getAllergies(), new TypeReference<List<String>>() {});
            d.setAllergies(allergies);
        } catch (Exception e) {
            logger.error("Error parsing allergies JSON for userId {}: {}", p.getUserId(), e.getMessage());
            d.setAllergies(Collections.emptyList());
        }
        d.setPregnant(p.getPregnant());
        try {
            List<String> goals = p.getGoals() == null ? Collections.emptyList()
                    : om.readValue(p.getGoals(), new TypeReference<List<String>>() {});
            d.setGoals(goals);
        } catch (Exception e) {
            logger.error("Error parsing goals JSON for userId {}: {}", p.getUserId(), e.getMessage());
            d.setGoals(Collections.emptyList());
        }
        try {
            Map<String, Object> lifestyle = p.getLifestyleJson() == null ? Collections.emptyMap()
                    : om.readValue(p.getLifestyleJson(), new TypeReference<Map<String, Object>>() {});
            d.setLifestyle(lifestyle);
        } catch (Exception e) {
            logger.error("Error parsing lifestyle JSON for userId {}: {}", p.getUserId(), e.getMessage());
            d.setLifestyle(Collections.emptyMap());
        }
        return d;
    }

    public Profile toEntity(Profile existing, ProfileDTO dto) {
        Profile p = existing == null ? new Profile() : existing;
        if (dto.getUserId() != null) p.setUserId(dto.getUserId());
        // Handle SkinType conversion from Enum to String safely
        p.setSkinType(dto.getSkinType() != null ? dto.getSkinType().name() : null);

        try { p.setConcerns(dto.getConcerns() == null ? null : om.writeValueAsString(dto.getConcerns())); } catch (Exception e) { logger.error("Error writing concerns JSON for userId {}: {}", dto.getUserId(), e.getMessage()); p.setConcerns(null); }
        try { p.setAllergies(dto.getAllergies() == null ? null : om.writeValueAsString(dto.getAllergies())); } catch (Exception e) { logger.error("Error writing allergies JSON for userId {}: {}", dto.getUserId(), e.getMessage()); p.setAllergies(null); }
        p.setPregnant(dto.getPregnant());
        try { p.setGoals(dto.getGoals() == null ? null : om.writeValueAsString(dto.getGoals())); } catch (Exception e) { logger.error("Error writing goals JSON for userId {}: {}", dto.getUserId(), e.getMessage()); p.setGoals(null); }
        try { p.setLifestyleJson(dto.getLifestyle() == null ? null : om.writeValueAsString(dto.getLifestyle())); } catch (Exception e) { logger.error("Error writing lifestyle JSON for userId {}: {}", dto.getUserId(), e.getMessage()); p.setLifestyleJson(null); }
        return p;
    }
}
