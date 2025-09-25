package com.example.demo.personalization.service;

import com.example.demo.personalization.dto.ProfileDTO;
import com.example.demo.personalization.model.Profile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class ProfileMapper {

    private final ObjectMapper om = new ObjectMapper();

    public ProfileDTO toDto(Profile p) {
        if (p == null) return null;
        ProfileDTO d = new ProfileDTO();
        d.setUserId(p.getUserId());
        d.setSkinType(p.getSkinType());
        try {
            List<String> concerns = p.getConcerns() == null ? Collections.emptyList()
                    : om.readValue(p.getConcerns(), new TypeReference<List<String>>() {});
            d.setConcerns(concerns);
        } catch (Exception e) {
            d.setConcerns(Collections.emptyList());
        }
        try {
            List<String> allergies = p.getAllergies() == null ? Collections.emptyList()
                    : om.readValue(p.getAllergies(), new TypeReference<List<String>>() {});
            d.setAllergies(allergies);
        } catch (Exception e) {
            d.setAllergies(Collections.emptyList());
        }
        d.setPregnant(p.getPregnant());
        try {
            List<String> goals = p.getGoals() == null ? Collections.emptyList()
                    : om.readValue(p.getGoals(), new TypeReference<List<String>>() {});
            d.setGoals(goals);
        } catch (Exception e) {
            d.setGoals(Collections.emptyList());
        }
        try {
            Map<String, Object> lifestyle = p.getLifestyleJson() == null ? Collections.emptyMap()
                    : om.readValue(p.getLifestyleJson(), new TypeReference<Map<String, Object>>() {});
            d.setLifestyle(lifestyle);
        } catch (Exception e) {
            d.setLifestyle(Collections.emptyMap());
        }
        return d;
    }

    public Profile toEntity(Profile existing, ProfileDTO dto) {
        Profile p = existing == null ? new Profile() : existing;
        if (dto.getUserId() != null) p.setUserId(dto.getUserId());
        p.setSkinType(dto.getSkinType());
        try { p.setConcerns(dto.getConcerns() == null ? null : om.writeValueAsString(dto.getConcerns())); } catch (Exception e) { p.setConcerns(null); }
        try { p.setAllergies(dto.getAllergies() == null ? null : om.writeValueAsString(dto.getAllergies())); } catch (Exception e) { p.setAllergies(null); }
        p.setPregnant(dto.getPregnant());
        try { p.setGoals(dto.getGoals() == null ? null : om.writeValueAsString(dto.getGoals())); } catch (Exception e) { p.setGoals(null); }
        try { p.setLifestyleJson(dto.getLifestyle() == null ? null : om.writeValueAsString(dto.getLifestyle())); } catch (Exception e) { p.setLifestyleJson(null); }
        return p;
    }
}

