package com.cloudtrim.waste.rules;

import com.cloudtrim.cloud.CloudAccount;
import com.cloudtrim.cloud.CloudResource;
import com.cloudtrim.waste.WasteFindingCandidate;
import com.cloudtrim.waste.WasteRule;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UnusedPublicIpRule implements WasteRule {
    @Override public String code() { return "UNUSED_PUBLIC_IP"; }

    @Override
    public Optional<WasteFindingCandidate> evaluate(CloudAccount account, CloudResource resource) {
        if ("PUBLIC_IP".equals(resource.getResourceType())
                && "ALLOCATED_IDLE".equalsIgnoreCase(resource.getStatus())) {
            return Optional.of(new WasteFindingCandidate(
                    "Unused public IPv4 address: " + resource.getName(),
                    "Allocated static public IPv4 address is not attached to a resource.",
                    "LOW", resource.getMonthlyCostUsd()));
        }
        return Optional.empty();
    }
}
