package com.cloudtrim.dashboard;

import com.cloudtrim.cloud.CloudResourceRepository;
import com.cloudtrim.common.TenantContext;
import com.cloudtrim.tenant.TenantRepository;
import com.cloudtrim.waste.WasteFinding;
import com.cloudtrim.waste.WasteFindingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;

@Service
public class DashboardService {
    private final CloudResourceRepository resourceRepository;
    private final WasteFindingRepository findingRepository;
    private final TenantRepository tenantRepository;
    private final SavingsSnapshotRepository snapshotRepository;
    private final BigDecimal usdInr;

    public DashboardService(CloudResourceRepository resourceRepository,
                            WasteFindingRepository findingRepository,
                            TenantRepository tenantRepository,
                            SavingsSnapshotRepository snapshotRepository,
                            @Value("${cloudtrim.exchange.usd-inr:83}") BigDecimal usdInr) {
        this.resourceRepository = resourceRepository;
        this.findingRepository = findingRepository;
        this.tenantRepository = tenantRepository;
        this.snapshotRepository = snapshotRepository;
        this.usdInr = usdInr;
    }

    public Map<String, Object> summary() {
        UUID tenantId = TenantContext.getRequired();
        BigDecimal spend = resourceRepository.findByTenantId(tenantId).stream()
                .map(resource -> resource.getMonthlyCostUsd()).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal waste = findingRepository.findByTenantIdAndStatus(tenantId, WasteFinding.Status.OPEN).stream()
                .map(WasteFinding::getMonthlyWasteUsd).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal realized = findingRepository.findByTenantIdAndStatus(tenantId, WasteFinding.Status.RESOLVED).stream()
                .map(WasteFinding::getMonthlyWasteUsd).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal savingsPercent = spend.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                : waste.divide(spend, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
        return Map.of(
                "totalMonthlyCloudSpendUsd", spend,
                "totalMonthlyCloudSpendInr", spend.multiply(usdInr).setScale(2, RoundingMode.HALF_UP),
                "detectedMonthlyWasteUsd", waste,
                "detectedMonthlyWasteInr", waste.multiply(usdInr).setScale(2, RoundingMode.HALF_UP),
                "savingsPercent", savingsPercent,
                "realizedSavingsUsd", realized,
                "realizedSavingsInr", realized.multiply(usdInr).setScale(2, RoundingMode.HALF_UP),
                "usdInrRate", usdInr);
    }
}
