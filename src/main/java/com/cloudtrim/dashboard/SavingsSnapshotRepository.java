package com.cloudtrim.dashboard;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface SavingsSnapshotRepository extends JpaRepository<SavingsSnapshot, UUID> {
    Optional<SavingsSnapshot> findByTenantIdAndSnapshotDate(UUID tenantId, LocalDate snapshotDate);
}
