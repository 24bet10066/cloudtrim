package com.cloudtrim.auth.dto;

import java.util.UUID;

public record AuthResponse(String accessToken, UUID tenantId, String tenantSlug, String role) {}
