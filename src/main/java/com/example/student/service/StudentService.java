package com.example.student.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.student.dto.StudentDTO;
import com.example.student.entity.Student;
import com.example.student.repository.StudentRepository;
import com.example.student.exception.ResourceNotFoundException;

@Service
public class StudentService {

    @Autowired
    private StudentRepository repo;

    public Student saveStudent(StudentDTO dto) {
        Student student = new Student();

        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setCourse(dto.getCourse());

        return repo.save(student);
    }

    public Page<StudentDTO> getStudents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> students = repo.findAll(pageable);

        return students.map(student -> {
            StudentDTO dto = new StudentDTO();
            dto.setId(student.getId());
            dto.setName(student.getName());
            dto.setEmail(student.getEmail());
            dto.setCourse(student.getCourse());
            return dto;
        });
    }

    public Page<StudentDTO> searchStudents(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> students = repo.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, pageable);

        return students.map(student -> {
            StudentDTO dto = new StudentDTO();
            dto.setId(student.getId());
            dto.setName(student.getName());
            dto.setEmail(student.getEmail());
            dto.setCourse(student.getCourse());
            return dto;
        });
    }

    public void deleteStudent(Long id) {
        Student existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));

        repo.delete(existing);
    }

    public Student updateStudent(Long id, Student updatedStudent) {
        Student existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));

        existing.setName(updatedStudent.getName());
        existing.setEmail(updatedStudent.getEmail());
        existing.setCourse(updatedStudent.getCourse());

        return repo.save(existing);
    }
}