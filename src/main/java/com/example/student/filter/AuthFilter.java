package com.example.student.filter;

import com.example.student.entity.UserSession;
import com.example.student.repository.UserSessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

/**
 * Lightweight session-cookie filter.
 *
 * Public paths (no auth required):
 *   - All static files (.html, .png, .css, .js, etc.)
 *   - POST /auth/login  and  POST /auth/register
 *
 * Protected paths (SESSION_TOKEN cookie required):
 *   - /students/**
 *   - GET /auth/me
 *   - POST /auth/logout
 *
 * On a valid session the institution's ID is stored as a request attribute
 * "institutionId" (Long) for controllers to use.
 */
@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private UserSessionRepository sessionRepo;

    // Exact paths that are always public (no cookie check)
    private static final Set<String> PUBLIC_API_PATHS = Set.of(
            "/auth/login",
            "/auth/register"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Static assets and public API paths pass through without auth
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // All other paths require a valid session cookie
        String token = extractCookie(request, "SESSION_TOKEN");
        if (token == null) {
            sendUnauthorized(response);
            return;
        }

        Optional<UserSession> sessionOpt = sessionRepo.findByToken(token);
        if (sessionOpt.isEmpty()) {
            sendUnauthorized(response);
            return;
        }

        UserSession session = sessionOpt.get();
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            sendUnauthorized(response);
            return;
        }

        // Inject institution ID so controllers can read it without touching cookies
        request.setAttribute("institutionId", session.getInstitutionId());
        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        // Static file extensions — frontend handles redirects for protected pages
        if (path.equals("/")
                || path.endsWith(".html")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".jpeg")
                || path.endsWith(".ico")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".woff")
                || path.endsWith(".woff2")
                || path.endsWith(".svg")) {
            return true;
        }
        // Specific public API endpoints
        return PUBLIC_API_PATHS.contains(path);
    }

    private String extractCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }

    private void sendUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Unauthorized. Please log in.\"}");
    }
}
