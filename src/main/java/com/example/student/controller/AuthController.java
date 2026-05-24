package com.example.student.controller;

import com.example.student.dto.LoginRequest;
import com.example.student.dto.RegisterRequest;
import com.example.student.entity.Institution;
import com.example.student.exception.ResourceNotFoundException;
import com.example.student.repository.InstitutionRepository;
import com.example.student.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private InstitutionRepository institutionRepo;

    /** Register a new institution account */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return Map.of("message", "Institution registered successfully. Please log in.");
    }

    /** Login — sets an HttpOnly session cookie on success */
    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody LoginRequest req,
                                     HttpServletResponse response) {
        String token = authService.login(req);
        Cookie cookie = buildSessionCookie(token, 7 * 24 * 60 * 60);
        response.addCookie(cookie);
        return Map.of("message", "Logged in successfully");
    }

    /** Logout — deletes session and clears the cookie */
    @PostMapping("/logout")
    public Map<String, String> logout(HttpServletRequest request,
                                      HttpServletResponse response) {
        String token = extractCookie(request, "SESSION_TOKEN");
        if (token != null) {
            authService.logout(token);
        }
        response.addCookie(buildSessionCookie("", 0));  // expire cookie
        return Map.of("message", "Logged out successfully");
    }

    /** Returns the currently authenticated institution's details */
    @GetMapping("/me")
    public Map<String, Object> me(HttpServletRequest request) {
        Long institutionId = (Long) request.getAttribute("institutionId");
        Institution inst = institutionRepo.findById(institutionId)
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found"));
        return Map.of(
                "id",    inst.getId(),
                "name",  inst.getName(),
                "email", inst.getEmail()
        );
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private Cookie buildSessionCookie(String value, int maxAge) {
        Cookie cookie = new Cookie("SESSION_TOKEN", value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    private String extractCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
