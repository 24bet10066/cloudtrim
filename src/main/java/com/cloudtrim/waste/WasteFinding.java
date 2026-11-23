package com.cloudtrim.waste;

import com.cloudtrim.cloud.CloudAccount;
import com.cloudtrim.cloud.CloudResource;
import com.cloudtrim.tenant.Tenant;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "waste_findings")
public class WasteFinding {
    public enum Status { OPEN, RESOLVED, IGNORED }
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id") private Tenant tenant;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cloud_account_id") private CloudAccount cloudAccount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cloud_resource_id") private CloudResource cloudResource;
    @Column(name = "rule_code", nullable = false) private String ruleCode;
    @Column(nullable = false) private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false) private Status status = Status.OPEN;
    @Column(nullable = false) private String severity;
    @Column(name = "monthly_waste_usd", nullable = false) private BigDecimal monthlyWasteUsd;
    @Column(name = "annual_waste_usd", nullable = false) private BigDecimal annualWasteUsd;
    @Column(name = "detected_at", nullable = false) private Instant detectedAt = Instant.now();
    @Column(name = "resolved_at") private Instant resolvedAt;
    @Column(name = "created_at", nullable = false) private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();

    protected WasteFinding() {}

    public WasteFinding(Tenant tenant, CloudAccount account, CloudResource resource,
                        String ruleCode, String title, String description, String severity,
                        BigDecimal monthlyWasteUsd) {
        this.id = UUID.randomUUID(); this.tenant = tenant; this.cloudAccount = account;
        this.cloudResource = resource; this.ruleCode = ruleCode; this.title = title;
        this.description = description; this.severity = severity;
        this.monthlyWasteUsd = monthlyWasteUsd;
        this.annualWasteUsd = monthlyWasteUsd.multiply(BigDecimal.valueOf(12));
    }

    public UUID getId() { return id; }
    public String getRuleCode() { return ruleCode; }
    public String getDescription() { return description; }
    public BigDecimal getAnnualWasteUsd() { return annualWasteUsd; }
    public Status getStatus() { return status; }
    public CloudResource getCloudResource() { return cloudResource; }
    public BigDecimal getMonthlyWasteUsd() { return monthlyWasteUsd; }
    public String getTitle() { return title; }
    public String getSeverity() { return severity; }
    public Instant getDetectedAt() { return detectedAt; }
    public void resolve(Status status) {
        this.status = status;
        this.resolvedAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}
