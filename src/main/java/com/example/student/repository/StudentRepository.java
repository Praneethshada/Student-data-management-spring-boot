package com.example.student.repository;

import com.example.student.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // List all students for a specific institution
    Page<Student> findByInstitutionId(Long institutionId, Pageable pageable);

    // Search by name or email, scoped to the institution
    @Query("SELECT s FROM Student s WHERE s.institutionId = :institutionId AND " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(s.email) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Student> searchByInstitutionId(@Param("institutionId") Long institutionId,
                                        @Param("query") String query,
                                        Pageable pageable);

    // Ownership-safe lookup — returns empty if the student belongs to a different institution
    Optional<Student> findByIdAndInstitutionId(Long id, Long institutionId);
}