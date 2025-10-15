package com.example.demo.service.impl;

import com.example.demo.personalization.dto.ProfileDTO;
import com.example.demo.model.Profile;
import com.example.demo.repository.ProfileRepository;
import com.example.demo.personalization.service.ProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.demo.service.ProfileService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    public ProfileDTO getProfile(Long userId) {
        Optional<Profile> p = profileRepository.findById(userId);
        return p.map(profileMapper::toDto).orElseGet(() -> {
            // return empty default profile
            ProfileDTO d = new ProfileDTO();
            d.setUserId(userId);
            d.setSkinType(null);
            d.setConcerns(Collections.emptyList());
            d.setAllergies(Collections.emptyList());
            d.setPregnant(false);
            d.setGoals(Collections.emptyList());
            d.setLifestyle(Collections.emptyMap());
            return d;
        });
    }

    public ProfileDTO updateProfile(Long userId, ProfileDTO body) {
        Profile existing = profileRepository.findById(userId).orElse(null);
        if (body.getUserId() == null) body.setUserId(userId);
        Profile toSave = profileMapper.toEntity(existing, body);
        Profile saved = profileRepository.save(toSave);
        return profileMapper.toDto(saved);
    }

    public Map<String, Object> getPrefs(Long userId) {
        // demo: return hardcoded prefs; extend to persist later
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("prefer_functions", List.of("HYDRATION", "ANTI_AGE"));
        prefs.put("avoid_ingredients", List.of("FRAGRANCE"));
        prefs.put("budget_min", 0);
        prefs.put("budget_max", 200);
        return prefs;
    }

    public void updatePrefs(Long userId, Map<String, Object> body) {
        // For MVP keep in-memory/no-op; you can persist to a new table later
    }

    public Map<String, Object> checkProduct(Long userId, String productId, ProfileDTO override) {
        ProfileDTO profile = (override != null) ? override : getProfile(userId);
        List<String> flags = new ArrayList<>();
        List<String> reasons = new ArrayList<>();

        // simple demo rules based on productId string (replace with real ingredient checks)
        String pid = productId == null ? "" : productId.toLowerCase();

        // pregnancy risk demo: if pregnant and productId contains "retinol" -> block
        if (Boolean.TRUE.equals(profile.getPregnant()) && pid.contains("retinol")) {
            flags.add("PREGNANCY_FLAG");
            reasons.add("Contains retinoids which are not recommended during pregnancy");
            return Map.of("fitScore", 0, "flags", flags, "reasons", reasons);
        }

        // allergy demo: if any allergy keyword appears in productId
        for (String a : profile.getAllergies()) {
            if (a == null) continue;
            String low = a.toLowerCase();
            if (!low.isBlank() && pid.contains(low)) {
                flags.add("ALLERGY_HIT");
                reasons.add("Allergy hit: " + a);
            }
        }

        int score = 80; // base
        if (pid.contains("alcohol") || pid.contains("fragrance") || pid.contains("phenoxy")) {
            score -= 30;
            reasons.add("Contains potentially irritating ingredients");
        }

        if (!flags.isEmpty()) {
            score = Math.max(0, score - 50);
        }

        // clamp
        score = Math.max(0, Math.min(100, score));

        if (reasons.isEmpty()) reasons.add("No major issues detected (demo rules)");

        return Map.of("fitScore", score, "flags", flags, "reasons", reasons);
    }
}

