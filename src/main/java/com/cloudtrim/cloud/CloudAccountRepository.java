package com.cloudtrim.cloud;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CloudAccountRepository extends JpaRepository<CloudAccount, UUID> {
    List<CloudAccount> findByTenantId(UUID tenantId);
    Optional<CloudAccount> findByIdAndTenantId(UUID id, UUID tenantId);
}
