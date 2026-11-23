package com.cloudtrim.waste;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WasteFindingRepository extends JpaRepository<WasteFinding, UUID> {
    List<WasteFinding> findByTenantId(UUID tenantId);
    List<WasteFinding> findByTenantIdAndStatus(UUID tenantId, WasteFinding.Status status);
    Optional<WasteFinding> findByIdAndTenantId(UUID id, UUID tenantId);
    boolean existsByTenantIdAndCloudResourceIdAndRuleCodeAndStatus(
            UUID tenantId, UUID resourceId, String ruleCode, WasteFinding.Status status);
}
