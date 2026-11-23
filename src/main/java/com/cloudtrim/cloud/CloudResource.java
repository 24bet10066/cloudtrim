package com.cloudtrim.cloud;

import com.cloudtrim.tenant.Tenant;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "cloud_resources")
public class CloudResource {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id") private Tenant tenant;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cloud_account_id") private CloudAccount cloudAccount;
    @Column(name = "provider_resource_id", nullable = false) private String providerResourceId;
    @Column(name = "resource_type", nullable = false) private String resourceType;
    @Column(nullable = false) private String name;
    private String region;
    private String environment;
    @Column(nullable = false) private String status;
    @Column(name = "monthly_cost_usd", nullable = false) private BigDecimal monthlyCostUsd = BigDecimal.ZERO;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> tags = Map.of();
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> metadata = Map.of();
    @Column(name = "discovered_at", nullable = false) private Instant discoveredAt = Instant.now();
    @Column(name = "last_seen_at", nullable = false) private Instant lastSeenAt = Instant.now();
    @Column(name = "created_at", nullable = false) private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();

    protected CloudResource() {}

    public CloudResource(UUID id, Tenant tenant, CloudAccount account, String providerResourceId,
                         String resourceType, String name, String region, String environment,
                         String status, BigDecimal monthlyCostUsd, Map<String, Object> metadata) {
        this.id = id; this.tenant = tenant; this.cloudAccount = account;
        this.providerResourceId = providerResourceId; this.resourceType = resourceType;
        this.name = name; this.region = region; this.environment = environment;
        this.status = status; this.monthlyCostUsd = monthlyCostUsd; this.metadata = metadata;
    }

    public UUID getId() { return id; }
    public CloudAccount getCloudAccount() { return cloudAccount; }
    public String getResourceType() { return resourceType; }
    public String getName() { return name; }
    public String getEnvironment() { return environment; }
    public String getStatus() { return status; }
    public BigDecimal getMonthlyCostUsd() { return monthlyCostUsd; }
    public Map<String, Object> getMetadata() { return metadata; }
}
