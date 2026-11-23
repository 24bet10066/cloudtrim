package com.cloudtrim.scanner;

import java.math.BigDecimal;
import java.util.Map;

public record ScannedResource(
        String providerResourceId,
        String resourceType,
        String name,
        String region,
        String environment,
        String status,
        BigDecimal monthlyCostUsd,
        Map<String, Object> metadata
) {}
