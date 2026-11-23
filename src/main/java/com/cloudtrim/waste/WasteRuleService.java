package com.cloudtrim.waste;

import com.cloudtrim.cloud.CloudAccount;
import com.cloudtrim.cloud.CloudAccountRepository;
import com.cloudtrim.cloud.CloudResource;
import com.cloudtrim.cloud.CloudResourceRepository;
import com.cloudtrim.common.TenantContext;
import com.cloudtrim.tenant.Tenant;
import com.cloudtrim.tenant.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class WasteRuleService {
    private final CloudResourceRepository resourceRepository;
    private final WasteFindingRepository findingRepository;
    private final CloudAccountRepository accountRepository;
    private final TenantRepository tenantRepository;
    private final List<WasteRule> rules;

    public WasteRuleService(CloudResourceRepository resourceRepository,
                            WasteFindingRepository findingRepository,
                            CloudAccountRepository accountRepository,
                            TenantRepository tenantRepository,
                            List<WasteRule> rules) {
        this.resourceRepository = resourceRepository;
        this.findingRepository = findingRepository;
        this.accountRepository = accountRepository;
        this.tenantRepository = tenantRepository;
        this.rules = rules;
    }

    @Transactional
    public int runRules() {
        UUID tenantId = TenantContext.getRequired();
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow();
        int added = 0;
        for (CloudResource resource : resourceRepository.findByTenantId(tenantId)) {
            CloudAccount account = resourceAccount(resource, tenantId);
            for (WasteRule rule : rules) {
                Optional<WasteFindingCandidate> candidate = rule.evaluate(account, resource);
                if (candidate.isPresent() && !findingRepository.existsByTenantIdAndCloudResourceIdAndRuleCodeAndStatus(
                        tenantId, resource.getId(), rule.code(), WasteFinding.Status.OPEN)) {
                    WasteFindingCandidate value = candidate.get();
                    findingRepository.save(new WasteFinding(tenant, account, resource, rule.code(),
                            value.title(), value.description(), value.severity(), value.monthlyWasteUsd()));
                    added++;
                }
            }
        }
        return added;
    }

    private CloudAccount resourceAccount(CloudResource resource, UUID tenantId) {
        return resource.getCloudAccount();
    }
}
