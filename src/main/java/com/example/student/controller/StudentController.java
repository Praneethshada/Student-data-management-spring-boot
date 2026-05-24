package com.example.student.controller;

import com.example.student.dto.StudentDTO;
import com.example.student.entity.Student;
import com.example.student.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService service;

    @Autowired
    private Validator validator;

    @PostMapping
    public Student addStudent(@Valid @RequestBody StudentDTO dto,
                              HttpServletRequest request) {
        validateDto(dto);
        Long institutionId = (Long) request.getAttribute("institutionId");
        return service.saveStudent(dto, institutionId);
    }

    @GetMapping
    public Page<StudentDTO> getStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String query,
            HttpServletRequest request) {

        Long institutionId = (Long) request.getAttribute("institutionId");
        if (query != null && !query.trim().isEmpty()) {
            return service.searchStudents(query.trim(), page, size, institutionId);
        }
        return service.getStudents(page, size, institutionId);
    }

    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Long id,
                                HttpServletRequest request) {
        Long institutionId = (Long) request.getAttribute("institutionId");
        service.deleteStudent(id, institutionId);
        return "Student deleted successfully";
    }

    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id,
                                 @Valid @RequestBody StudentDTO dto,
                                 HttpServletRequest request) {
        validateDto(dto);
        Long institutionId = (Long) request.getAttribute("institutionId");

        Student student = new Student();
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setCourse(dto.getCourse());

        return service.updateStudent(id, student, institutionId);
    }

    private void validateDto(StudentDTO dto) {
        Set<ConstraintViolation<StudentDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<>(violations));
        }
    }
}