package com.cloudtrim.waste;

import java.math.BigDecimal;

public record WasteFindingCandidate(
        String title,
        String description,
        String severity,
        BigDecimal monthlyWasteUsd
) {}
