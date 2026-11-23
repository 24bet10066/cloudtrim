package com.cloudtrim.dashboard;

import com.cloudtrim.tenant.Tenant;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "savings_snapshots")
public class SavingsSnapshot {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id") private Tenant tenant;
    @Column(name = "snapshot_date", nullable = false) private LocalDate snapshotDate;
    @Column(name = "total_monthly_spend_usd", nullable = false) private BigDecimal totalMonthlySpendUsd;
    @Column(name = "detected_monthly_waste_usd", nullable = false) private BigDecimal detectedMonthlyWasteUsd;
    @Column(name = "realized_savings_usd", nullable = false) private BigDecimal realizedSavingsUsd;
    @Column(name = "potential_savings_usd", nullable = false) private BigDecimal potentialSavingsUsd;
    @Column(name = "created_at", nullable = false) private Instant createdAt = Instant.now();

    protected SavingsSnapshot() {}

    public SavingsSnapshot(Tenant tenant, LocalDate snapshotDate, BigDecimal totalMonthlySpendUsd,
                           BigDecimal detectedMonthlyWasteUsd, BigDecimal realizedSavingsUsd,
                           BigDecimal potentialSavingsUsd) {
        this.id = UUID.randomUUID(); this.tenant = tenant; this.snapshotDate = snapshotDate;
        this.totalMonthlySpendUsd = totalMonthlySpendUsd; this.detectedMonthlyWasteUsd = detectedMonthlyWasteUsd;
        this.realizedSavingsUsd = realizedSavingsUsd; this.potentialSavingsUsd = potentialSavingsUsd;
    }
}
