package com.cloudtrim.waste.rules;

import com.cloudtrim.cloud.CloudAccount;
import com.cloudtrim.cloud.CloudResource;
import com.cloudtrim.waste.WasteFindingCandidate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WasteRulesTest {
    private CloudResource resource(String type, String status, String environment,
                                    BigDecimal cost, Map<String, Object> metadata) {
        return new CloudResource(null, null, null, "provider-1", type, "test-resource",
                "us-east-1", environment, status, cost, metadata);
    }

    @Test void idleVmRequiresRunningCpuAndAgeThresholds() {
        IdleVmRule rule = new IdleVmRule();
        assertTrue(rule.evaluate(null, resource("VM", "RUNNING", "prod",
                BigDecimal.valueOf(100), Map.of("averageCpuPercent", 4, "runningDays", 7))).isPresent());
        assertTrue(rule.evaluate(null, resource("VM", "RUNNING", "prod",
                BigDecimal.valueOf(100), Map.of("averageCpuPercent", 5, "runningDays", 7))).isEmpty());
        assertTrue(rule.evaluate(null, resource("VM", "RUNNING", "prod",
                BigDecimal.valueOf(100), Map.of("averageCpuPercent", 4, "runningDays", 6))).isEmpty());
    }

    @Test void unattachedStorageRequiresUnattachedVolume() {
        UnattachedStorageRule rule = new UnattachedStorageRule();
        assertTrue(rule.evaluate(null, resource("STORAGE_VOLUME", "UNATTACHED", "dev",
                BigDecimal.TEN, Map.of())).isPresent());
        assertTrue(rule.evaluate(null, resource("STORAGE_VOLUME", "ATTACHED", "dev",
                BigDecimal.TEN, Map.of())).isEmpty());
    }

    @Test void unusedPublicIpOnlyMatchesIdleAllocation() {
        UnusedPublicIpRule rule = new UnusedPublicIpRule();
        assertTrue(rule.evaluate(null, resource("PUBLIC_IP", "ALLOCATED_IDLE", "dev",
                BigDecimal.valueOf(3.65), Map.of())).isPresent());
        assertTrue(rule.evaluate(null, resource("PUBLIC_IP", "ATTACHED", "dev",
                BigDecimal.valueOf(3.65), Map.of())).isEmpty());
    }

    @Test void oversizedDatabaseRequiresNonProdHighTierAndContinuousUse() {
        OversizedNonProdDatabaseRule rule = new OversizedNonProdDatabaseRule();
        assertTrue(rule.evaluate(null, resource("DATABASE", "RUNNING", "staging",
                BigDecimal.valueOf(450), Map.of("tier", "high", "running24x7", true))).isPresent());
        assertTrue(rule.evaluate(null, resource("DATABASE", "RUNNING", "prod",
                BigDecimal.valueOf(450), Map.of("tier", "high", "running24x7", true))).isEmpty());
    }
}
