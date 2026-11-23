package com.cloudtrim.cloud;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CloudResourceRepository extends JpaRepository<CloudResource, UUID> {
    List<CloudResource> findByTenantId(UUID tenantId);
    void deleteByTenantIdAndCloudAccountId(UUID tenantId, UUID cloudAccountId);
}
