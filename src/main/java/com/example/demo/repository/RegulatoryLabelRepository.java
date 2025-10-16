package com.example.demo.repository;

import com.example.demo.model.RegulatoryLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegulatoryLabelRepository extends JpaRepository<RegulatoryLabel, Long> {
}
