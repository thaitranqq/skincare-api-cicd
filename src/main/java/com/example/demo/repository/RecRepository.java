package com.example.demo.repository;

import com.example.demo.model.Rec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecRepository extends JpaRepository<Rec, Long> {
    List<Rec> findByUserId(Long userId);
    List<Rec> findByProductId(Long productId);
}
