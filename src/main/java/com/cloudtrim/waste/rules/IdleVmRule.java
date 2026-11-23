package com.cloudtrim.waste.rules;

import com.cloudtrim.cloud.CloudAccount;
import com.cloudtrim.cloud.CloudResource;
import com.cloudtrim.waste.WasteFindingCandidate;
import com.cloudtrim.waste.WasteRule;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class IdleVmRule implements WasteRule {
    @Override public String code() { return "IDLE_VM"; }

    @Override
    public Optional<WasteFindingCandidate> evaluate(CloudAccount account, CloudResource resource) {
        if (!"VM".equals(resource.getResourceType()) || !"RUNNING".equalsIgnoreCase(resource.getStatus())) {
            return Optional.empty();
        }
        Object cpu = resource.getMetadata().get("averageCpuPercent");
        Object days = resource.getMetadata().get("runningDays");
        if (cpu instanceof Number cpuValue && days instanceof Number dayValue
                && cpuValue.doubleValue() < 5 && dayValue.intValue() >= 7) {
            return Optional.of(new WasteFindingCandidate(
                    "Idle virtual machine: " + resource.getName(),
                    "VM has average CPU below 5% for at least 7 days.",
                    "HIGH", resource.getMonthlyCostUsd()));
        }
        return Optional.empty();
    }
}
