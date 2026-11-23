package com.cloudtrim.waste.rules;

import com.cloudtrim.cloud.CloudAccount;
import com.cloudtrim.cloud.CloudResource;
import com.cloudtrim.waste.WasteFindingCandidate;
import com.cloudtrim.waste.WasteRule;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OversizedNonProdDatabaseRule implements WasteRule {
    @Override public String code() { return "OVERSIZED_NON_PROD_DATABASE"; }

    @Override
    public Optional<WasteFindingCandidate> evaluate(CloudAccount account, CloudResource resource) {
        String environment = resource.getEnvironment();
        Object tier = resource.getMetadata().get("tier");
        Object running24x7 = resource.getMetadata().get("running24x7");
        boolean nonProd = environment != null
                && ("staging".equalsIgnoreCase(environment) || "dev".equalsIgnoreCase(environment));
        if ("DATABASE".equals(resource.getResourceType()) && nonProd
                && "high".equalsIgnoreCase(String.valueOf(tier))
                && Boolean.TRUE.equals(running24x7)) {
            return Optional.of(new WasteFindingCandidate(
                    "Oversized non-production database: " + resource.getName(),
                    "High-tier staging or development database is running continuously.",
                    "MEDIUM", resource.getMonthlyCostUsd().multiply(java.math.BigDecimal.valueOf(0.55))));
        }
        return Optional.empty();
    }
}
