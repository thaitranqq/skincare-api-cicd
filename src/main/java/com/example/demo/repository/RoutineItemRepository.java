package com.example.demo.repository;

import com.example.demo.model.RoutineItem;
import com.example.demo.model.RoutineItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutineItemRepository extends JpaRepository<RoutineItem, RoutineItemId> {
    List<RoutineItem> findByRoutineId(Long routineId);
    List<RoutineItem> findByProductId(Long productId);
}
