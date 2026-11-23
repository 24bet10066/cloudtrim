package com.cloudtrim.dashboard;

import com.cloudtrim.waste.WasteFinding;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record FindingResponse(
        UUID id,
        String ruleCode,
        String title,
        String description,
        String status,
        String severity,
        BigDecimal monthlyWasteUsd,
        BigDecimal annualWasteUsd,
        String resourceName,
        Instant detectedAt
) {
    public static FindingResponse from(WasteFinding finding) {
        String resourceName = null;
        return new FindingResponse(
                finding.getId(),
                finding.getRuleCode(),
                finding.getTitle(),
                finding.getDescription(),
                finding.getStatus().name(),
                finding.getSeverity(),
                finding.getMonthlyWasteUsd(),
                finding.getAnnualWasteUsd(),
                resourceName,
                finding.getDetectedAt());
    }
}
