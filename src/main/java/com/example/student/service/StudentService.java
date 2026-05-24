package com.example.student.service;

import com.example.student.dto.StudentDTO;
import com.example.student.entity.Student;
import com.example.student.exception.ResourceNotFoundException;
import com.example.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    @Autowired
    private StudentRepository repo;

    public Student saveStudent(StudentDTO dto, Long institutionId) {
        Student student = new Student();
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setCourse(dto.getCourse());
        student.setInstitutionId(institutionId);
        return repo.save(student);
    }

    public Page<StudentDTO> getStudents(int page, int size, Long institutionId) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findByInstitutionId(institutionId, pageable).map(this::toDTO);
    }

    public Page<StudentDTO> searchStudents(String query, int page, int size, Long institutionId) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.searchByInstitutionId(institutionId, query, pageable).map(this::toDTO);
    }

    public void deleteStudent(Long id, Long institutionId) {
        // findByIdAndInstitutionId ensures a student from another institution
        // cannot be deleted — it simply returns empty → 404
        Student existing = repo.findByIdAndInstitutionId(id, institutionId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));
        repo.delete(existing);
    }

    public Student updateStudent(Long id, Student updatedStudent, Long institutionId) {
        Student existing = repo.findByIdAndInstitutionId(id, institutionId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));

        existing.setName(updatedStudent.getName());
        existing.setEmail(updatedStudent.getEmail());
        existing.setCourse(updatedStudent.getCourse());
        return repo.save(existing);
    }

    private StudentDTO toDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setCourse(student.getCourse());
        return dto;
    }
}