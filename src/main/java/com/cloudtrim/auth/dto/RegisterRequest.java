package com.cloudtrim.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 255) String tenantName,
        @NotBlank @Size(max = 100) String tenantSlug,
        @Email @NotBlank String email,
        @Size(min = 8, max = 100) String password
) {}
