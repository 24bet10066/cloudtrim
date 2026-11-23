package com.cloudtrim.cloud;

import com.cloudtrim.tenant.Tenant;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cloud_accounts")
public class CloudAccount {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id") private Tenant tenant;
    @Column(nullable = false) private String provider;
    @Column(name = "external_account_id", nullable = false) private String externalAccountId;
    @Column(name = "display_name", nullable = false) private String displayName;
    @Column(nullable = false) private String status = "ACTIVE";
    @Column(name = "credentials_encrypted") private String credentialsEncrypted;
    @Column(name = "last_scan_at") private Instant lastScanAt;
    @Column(name = "created_at", nullable = false) private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();

    protected CloudAccount() {}

    public CloudAccount(UUID id, Tenant tenant, String provider, String externalAccountId,
                        String displayName) {
        this.id = id;
        this.tenant = tenant;
        this.provider = provider;
        this.externalAccountId = externalAccountId;
        this.displayName = displayName;
    }
    public UUID getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public String getProvider() { return provider; }
    public String getDisplayName() { return displayName; }
    public void markScanned() { lastScanAt = Instant.now(); updatedAt = Instant.now(); }
}
