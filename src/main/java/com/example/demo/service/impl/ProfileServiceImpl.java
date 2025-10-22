package com.example.demo.service.impl;

import com.example.demo.exception.InvalidRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.personalization.dto.ProfileDTO;
import com.example.demo.model.Profile;
import com.example.demo.repository.ProfileRepository;
import com.example.demo.personalization.service.ProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.demo.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileServiceImpl.class);

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    public ProfileDTO getProfile(Long userId) {
        try {
            Optional<Profile> p = profileRepository.findById(userId);
            if (p.isPresent()) {
                return profileMapper.toDto(p.get());
            } else {
                return createDefaultProfileDTO(userId);
            }
        } catch (IllegalArgumentException e) {
            // This catches issues like invalid enum values in the database (e.g., SkinType)
            logger.error("Data integrity issue: Invalid enum value found for userId {}. Returning default profile. Error: {}", userId, e.getMessage());
            return createDefaultProfileDTO(userId);
        }
    }

    private ProfileDTO createDefaultProfileDTO(Long userId) {
        ProfileDTO d = new ProfileDTO();
        d.setUserId(userId);
        d.setSkinType(null);
        d.setConcerns(Collections.emptyList());
        d.setAllergies(Collections.emptyList());
        d.setPregnant(false);
        d.setGoals(Collections.emptyList());
        d.setLifestyle(Collections.emptyMap());
        return d;
    }

    public ProfileDTO updateProfile(Long userId, ProfileDTO body) {
        Profile existing = profileRepository.findById(userId).orElse(null);
        body.setUserId(Objects.requireNonNullElse(body.getUserId(), userId));
        Profile toSave = profileMapper.toEntity(existing, body);
        Profile saved = profileRepository.save(toSave);
        return profileMapper.toDto(saved);
    }

    public Map<String, Object> getPrefs(Long userId) {
        ProfileDTO profile = getProfile(userId);
        Map<String, Object> prefs = new HashMap<>();

        prefs.put("prefer_functions", profile.getGoals() != null ? profile.getGoals() : Collections.emptyList());
        prefs.put("avoid_ingredients", profile.getAllergies() != null ? profile.getAllergies() : Collections.emptyList());

        Map<String, Object> lifestyle = profile.getLifestyle() != null ? profile.getLifestyle() : Collections.emptyMap();
        prefs.put("budget_min", lifestyle.getOrDefault("budget_min", 0));
        prefs.put("budget_max", lifestyle.getOrDefault("budget_max", 200));

        return prefs;
    }

    @SuppressWarnings("unchecked")
    public void updatePrefs(Long userId, Map<String, Object> body) {
        ProfileDTO profile = getProfile(userId);

        if (body.containsKey("prefer_functions")) {
            try {
                profile.setGoals((List<String>) body.get("prefer_functions"));
            } catch (ClassCastException e) {
                throw new InvalidRequestException("Invalid format for prefer_functions");
            }
        }

        if (body.containsKey("avoid_ingredients")) {
            try {
                profile.setAllergies((List<String>) body.get("avoid_ingredients"));
            } catch (ClassCastException e) {
                throw new InvalidRequestException("Invalid format for avoid_ingredients");
            }
        }

        Map<String, Object> lifestyle = profile.getLifestyle() != null ? new HashMap<>(profile.getLifestyle()) : new HashMap<>();
        boolean lifestyleUpdated = false;
        if (body.containsKey("budget_min")) {
            lifestyle.put("budget_min", body.get("budget_min"));
            lifestyleUpdated = true;
        }
        if (body.containsKey("budget_max")) {
            lifestyle.put("budget_max", body.get("budget_max"));
            lifestyleUpdated = true;
        }

        if (lifestyleUpdated) {
            profile.setLifestyle(lifestyle);
        }

        updateProfile(userId, profile);
    }

    public Map<String, Object> checkProduct(Long userId, String productId, ProfileDTO override) {
        ProfileDTO profile = (override != null) ? override : getProfile(userId);
        List<String> flags = new ArrayList<>();
        List<String> reasons = new ArrayList<>();
        int score = 100; // Start with a perfect score

        // Normalize product ID for checks
        String pid = productId == null ? "" : productId.toLowerCase();

        // --- Apply Rules ---

        // 1. Pregnancy Risk Check
        score = checkPregnancyRisk(profile, pid, flags, reasons, score);
        if (score == 0) { // If pregnancy risk is critical, stop further checks
            return Map.of("fitScore", 0, "flags", flags, "reasons", reasons);
        }

        // 2. Allergy Check
        score = checkAllergies(profile, pid, flags, reasons, score);

        // 3. Irritating Ingredients Check
        score = checkIrritatingIngredients(pid, flags, reasons, score);
        
        // 4. Goals Match (Positive scoring)
        score = checkGoalsMatch(profile, pid, reasons, score);

        // 5. Budget Check (Demo: assuming a product price)
        // In a real scenario, you would fetch the actual product price here.
        double productPrice = getDemoProductPrice(productId); 
        score = checkBudget(profile, productPrice, reasons, score);


        // --- Final Score Adjustment and Clamping ---
        score = Math.max(0, Math.min(100, score)); // Clamp score between 0 and 100

        if (reasons.isEmpty()) {
            reasons.add("No major issues detected (demo rules).");
        }

        return Map.of("fitScore", score, "flags", flags, "reasons", reasons);
    }

    // --- Helper Methods for checkProduct ---

    private int checkPregnancyRisk(ProfileDTO profile, String pid, List<String> flags, List<String> reasons, int currentScore) {
        if (Boolean.TRUE.equals(profile.getPregnant()) && pid.contains("retinol")) {
            flags.add("PREGNANCY_FLAG");
            reasons.add("Contains retinoids which are not recommended during pregnancy.");
            return 0; // Critical flag, set score to 0
        }
        return currentScore;
    }

    private int checkAllergies(ProfileDTO profile, String pid, List<String> flags, List<String> reasons, int currentScore) {
        if (profile.getAllergies() != null) {
            for (String a : profile.getAllergies()) {
                if (a == null) continue;
                String low = a.toLowerCase();
                if (!low.isBlank() && pid.contains(low)) {
                    flags.add("ALLERGY_HIT");
                    reasons.add("Allergy hit: " + a + " found in product.");
                    currentScore -= 40; // Significant deduction for allergies
                }
            }
        }
        return currentScore;
    }

    private int checkIrritatingIngredients(String pid, List<String> flags, List<String> reasons, int currentScore) {
        // More specific checks could be added here, e.g., based on ingredient lists
        if (pid.contains("alcohol") || pid.contains("fragrance") || pid.contains("phenoxy")) {
            flags.add("POTENTIALLY_IRRITATING_INGREDIENTS");
            reasons.add("Contains potentially irritating ingredients (alcohol, fragrance, phenoxy).");
            currentScore -= 20; // Moderate deduction
        }
        return currentScore;
    }

    private int checkGoalsMatch(ProfileDTO profile, String pid, List<String> reasons, int currentScore) {
        if (profile.getGoals() != null && !profile.getGoals().isEmpty()) {
            // Demo: Simple check if product ID contains keywords related to goals
            // In a real system, this would involve matching product benefits/ingredients to user goals
            List<String> matchedGoals = profile.getGoals().stream()
                .filter(goal -> {
                    String lowerGoal = goal.toLowerCase();
                    // Example: if goal is HYDRATION, check for "hyaluron" or "glycerin"
                    // if goal is ANTI_AGE, check for "peptide" or "retinol" (if not pregnant)
                    if (lowerGoal.equals("hydration") && (pid.contains("hyaluron") || pid.contains("glycerin"))) return true;
                    if (lowerGoal.equals("anti_age") && (pid.contains("peptide") || pid.contains("retinol"))) return true;
                    // Add more goal-product keyword mappings here
                    return false;
                })
                .toList(); // Changed to toList()

            if (!matchedGoals.isEmpty()) {
                reasons.add("Product aligns with goals: " + String.join(", ", matchedGoals) + ".");
                currentScore += 15 * matchedGoals.size(); // Boost score for matching goals
            }
        }
        return currentScore;
    }

    private int checkBudget(ProfileDTO profile, double productPrice, List<String> reasons, int currentScore) {
        Map<String, Object> lifestyle = profile.getLifestyle();
        if (lifestyle != null) {
            // Safely cast to Number and then to Integer, or handle potential ClassCastException
            Integer budgetMin = ((Number) lifestyle.getOrDefault("budget_min", 0)).intValue();
            Integer budgetMax = ((Number) lifestyle.getOrDefault("budget_max", 200)).intValue(); // Default max if not set

            if (productPrice < budgetMin) {
                reasons.add("Product price (" + productPrice + ") is below preferred budget minimum (" + budgetMin + ").");
                currentScore -= 5; // Small deduction for being too cheap (might imply lower quality for some)
            } else if (productPrice > budgetMax) {
                reasons.add("Product price (" + productPrice + ") exceeds preferred budget maximum (" + budgetMax + ").");
                currentScore -= 10; // Moderate deduction for exceeding budget
            } else {
                reasons.add("Product price (" + productPrice + ") is within preferred budget range (" + budgetMin + " - " + budgetMax + ").");
            }
        }
        return currentScore;
    }

    // Demo method to simulate fetching product price
    private double getDemoProductPrice(String productId) {
        // In a real application, this would query a product database
        if (productId != null && productId.contains("premium")) {
            return 150.0;
        } else if (productId != null && productId.contains("budget")) {
            return 25.0;
        }
        return 75.0; // Default price
    }
}
