package com.cloudtrim.scanner;

import com.cloudtrim.cloud.CloudAccount;
import com.cloudtrim.cloud.CloudAccountRepository;
import com.cloudtrim.cloud.CloudResource;
import com.cloudtrim.cloud.CloudResourceRepository;
import com.cloudtrim.common.TenantContext;
import com.cloudtrim.tenant.TenantRepository;
import com.cloudtrim.common.TenantContext;
import com.cloudtrim.waste.WasteRuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CloudAccountService {
    private final CloudAccountRepository accountRepository;
    private final CloudResourceRepository resourceRepository;
    private final TenantRepository tenantRepository;
    private final DemoCloudScanner demoScanner;
    private final WasteRuleService wasteRuleService;

    public CloudAccountService(CloudAccountRepository accountRepository,
                               CloudResourceRepository resourceRepository,
                               TenantRepository tenantRepository, DemoCloudScanner demoScanner,
                               WasteRuleService wasteRuleService) {
        this.accountRepository = accountRepository;
        this.resourceRepository = resourceRepository;
        this.tenantRepository = tenantRepository;
        this.demoScanner = demoScanner;
        this.wasteRuleService = wasteRuleService;
    }

    @Transactional
    public int runDemoScan() {
        UUID tenantId = TenantContext.getRequired();
        var tenant = tenantRepository.findById(tenantId).orElseThrow();
        CloudAccount account = accountRepository.findByTenantId(tenantId).stream()
                .filter(a -> "DEMO".equals(a.getProvider()))
                .findFirst()
                .orElseGet(() -> accountRepository.save(
                        new CloudAccount(UUID.randomUUID(), tenant, "DEMO", "demo-account", "Live Demo Account")));
        resourceRepository.deleteByTenantIdAndCloudAccountId(tenantId, account.getId());
        int count = 0;
        for (ScannedResource scanned : demoScanner.scan()) {
            resourceRepository.save(new CloudResource(UUID.randomUUID(), tenant, account,
                    scanned.providerResourceId(), scanned.resourceType(), scanned.name(),
                    scanned.region(), scanned.environment(), scanned.status(),
                    scanned.monthlyCostUsd(), scanned.metadata()));
            count++;
        }
        account.markScanned();
        accountRepository.save(account);
        wasteRuleService.runRules();
        return count;
    }
}
