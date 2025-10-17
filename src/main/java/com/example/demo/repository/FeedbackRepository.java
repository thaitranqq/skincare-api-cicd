package com.example.demo.repository;

import com.example.demo.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    @Query(value = "SELECT id, user_id, product_id, rating, note, reaction_tags, created_at FROM feedback WHERE user_id = :userId ORDER BY created_at DESC", nativeQuery = true)
    List<Feedback> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    List<Feedback> findByUserId(Long userId);
    List<Feedback> findByProductId(Long productId);
}
