package com.cloudtrim.waste.rules;

import com.cloudtrim.cloud.CloudAccount;
import com.cloudtrim.cloud.CloudResource;
import com.cloudtrim.waste.WasteFindingCandidate;
import com.cloudtrim.waste.WasteRule;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UnattachedStorageRule implements WasteRule {
    @Override public String code() { return "UNATTACHED_VOLUME"; }

    @Override
    public Optional<WasteFindingCandidate> evaluate(CloudAccount account, CloudResource resource) {
        if ("STORAGE_VOLUME".equals(resource.getResourceType())
                && "UNATTACHED".equalsIgnoreCase(resource.getStatus())) {
            return Optional.of(new WasteFindingCandidate(
                    "Unattached storage volume: " + resource.getName(),
                    "Provisioned storage volume is not attached to an active VM.",
                    "MEDIUM", resource.getMonthlyCostUsd()));
        }
        return Optional.empty();
    }
}
