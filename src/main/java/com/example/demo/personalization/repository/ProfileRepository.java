package com.example.demo.personalization.repository;

import com.example.demo.personalization.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
