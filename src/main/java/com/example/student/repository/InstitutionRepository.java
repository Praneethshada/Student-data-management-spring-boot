package com.example.student.repository;

import com.example.student.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    Optional<Institution> findByEmail(String email);
    boolean existsByEmail(String email);
}
