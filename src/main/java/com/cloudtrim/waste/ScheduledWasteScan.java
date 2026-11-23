package com.cloudtrim.waste;

import com.cloudtrim.cloud.CloudAccountRepository;
import com.cloudtrim.common.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@EnableScheduling
public class ScheduledWasteScan {
    private static final Logger log = LoggerFactory.getLogger(ScheduledWasteScan.class);
    private final CloudAccountRepository accountRepository;
    private final WasteRuleService wasteRuleService;

    public ScheduledWasteScan(CloudAccountRepository accountRepository, WasteRuleService wasteRuleService) {
        this.accountRepository = accountRepository;
        this.wasteRuleService = wasteRuleService;
    }

    @Scheduled(cron = "${cloudtrim.schedule.daily-cron:0 0 2 * * *}")
    public void runDaily() {
        for (UUID tenantId : accountRepository.findAll().stream()
                .map(account -> account.getTenant().getId()).distinct().toList()) {
            TenantContext.set(tenantId);
            try {
                int findings = wasteRuleService.runRules();
                log.info("Daily waste scan tenant={} open findings added={}", tenantId, findings);
            } finally {
                TenantContext.clear();
            }
        }
    }
}
