package com.example.student.service;

import com.example.student.dto.LoginRequest;
import com.example.student.dto.RegisterRequest;
import com.example.student.entity.Institution;
import com.example.student.entity.UserSession;
import com.example.student.repository.InstitutionRepository;
import com.example.student.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private InstitutionRepository institutionRepo;

    @Autowired
    private UserSessionRepository sessionRepo;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Register a new institution. Throws IllegalArgumentException if the email
     * is already taken.
     */
    @Transactional
    public Institution register(RegisterRequest req) {
        if (institutionRepo.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists");
        }
        Institution institution = new Institution();
        institution.setName(req.getName());
        institution.setEmail(req.getEmail());
        institution.setPasswordHash(encoder.encode(req.getPassword()));
        return institutionRepo.save(institution);
    }

    /**
     * Validates credentials and creates a 7-day session token.
     * Returns the raw token string (caller sets it as a cookie).
     */
    @Transactional
    public String login(LoginRequest req) {
        Institution institution = institutionRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!encoder.matches(req.getPassword(), institution.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        UserSession session = new UserSession();
        session.setToken(UUID.randomUUID().toString());
        session.setInstitutionId(institution.getId());
        session.setExpiresAt(LocalDateTime.now().plusDays(7));
        sessionRepo.save(session);

        return session.getToken();
    }

    /**
     * Deletes the session for the given token (logout).
     */
    @Transactional
    public void logout(String token) {
        sessionRepo.deleteByToken(token);
    }
}
