package com.cloudtrim.dashboard;

import com.cloudtrim.common.TenantContext;
import com.cloudtrim.waste.WasteFinding;
import com.cloudtrim.waste.WasteFindingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class WeeklyDigestService {
    private static final Logger log = LoggerFactory.getLogger(WeeklyDigestService.class);
    private final WasteFindingRepository findingRepository;
    private final DashboardService dashboardService;

    public WeeklyDigestService(WasteFindingRepository findingRepository, DashboardService dashboardService) {
        this.findingRepository = findingRepository;
        this.dashboardService = dashboardService;
    }

    public String generate() {
        UUID tenantId = TenantContext.getRequired();
        List<WasteFinding> open = findingRepository.findByTenantIdAndStatus(
                tenantId, WasteFinding.Status.OPEN);
        BigDecimal waste = open.stream().map(WasteFinding::getMonthlyWasteUsd)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        String digest = "CloudTrim Weekly Savings Digest\n"
                + "Open findings: " + open.size() + "\n"
                + "Potential monthly waste: $" + waste + "\n"
                + "Dashboard summary: " + dashboardService.summary();
        log.info("Weekly savings digest tenant={}:\n{}", tenantId, digest);
        return digest;
    }
}
