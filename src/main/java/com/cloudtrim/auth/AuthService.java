package com.cloudtrim.auth;

import com.cloudtrim.auth.dto.AuthResponse;
import com.cloudtrim.auth.dto.LoginRequest;
import com.cloudtrim.auth.dto.RegisterRequest;
import com.cloudtrim.tenant.Tenant;
import com.cloudtrim.tenant.TenantRepository;
import com.cloudtrim.user.User;
import com.cloudtrim.user.UserRepository;
import com.cloudtrim.user.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthService {
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(TenantRepository tenantRepository, UserRepository userRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String slug = request.tenantSlug().trim().toLowerCase();
        if (tenantRepository.existsBySlug(slug)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tenant slug already exists");
        }
        Tenant tenant = tenantRepository.save(new Tenant(UUID.randomUUID(), request.tenantName().trim(), slug));
        if (userRepository.existsByTenantIdAndEmail(tenant.getId(), request.email().trim().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        User user = new User(UUID.randomUUID(), tenant, request.email().trim().toLowerCase(),
                passwordEncoder.encode(request.password()), UserRole.ADMIN);
        userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(user), tenant.getId(), tenant.getSlug(), "ADMIN");
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        User user;
        if (request.tenantSlug() == null || request.tenantSlug().isBlank()) {
            user = userRepository.findByEmail(email).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        } else {
            Tenant tenant = tenantRepository.findBySlug(request.tenantSlug().trim().toLowerCase())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
            user = userRepository.findByTenantIdAndEmail(tenant.getId(), email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        }
        if (!user.isEnabled() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return new AuthResponse(jwtService.generateToken(user), user.getTenant().getId(),
                user.getTenant().getSlug(), user.getRole().name());
    }
}
