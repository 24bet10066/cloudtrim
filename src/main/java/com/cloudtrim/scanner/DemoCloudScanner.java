package com.cloudtrim.scanner;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class DemoCloudScanner implements CloudScanner {
    @Override
    public String mode() {
        return "DEMO";
    }

    @Override
    public List<ScannedResource> scan() {
        List<ScannedResource> resources = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            String env = i <= 4 ? "prod" : i <= 8 ? "staging" : "dev";
            boolean idle = i == 2 || i == 7 || i == 11;
            resources.add(new ScannedResource("i-demo-" + i, "VM", "app-" + env + "-" + i,
                    "us-east-1", env, "RUNNING", idle ? new BigDecimal("120.00") : new BigDecimal("90.00"),
                    Map.of("averageCpuPercent", idle ? 3 : 35 + i, "runningDays", idle ? 9 : 30)));
        }
        for (int i = 1; i <= 8; i++) {
            resources.add(new ScannedResource("vol-demo-" + i, "STORAGE_VOLUME", "disk-" + i,
                    "us-east-1", i <= 3 ? "prod" : i <= 6 ? "staging" : "dev",
                    i == 2 || i == 5 || i == 8 ? "UNATTACHED" : "ATTACHED",
                    new BigDecimal("20.00"), Map.of("sizeGb", 100 + i * 50)));
        }
        for (int i = 1; i <= 5; i++) {
            resources.add(new ScannedResource("ip-demo-" + i, "PUBLIC_IP", "static-ip-" + i,
                    "us-east-1", i <= 2 ? "prod" : "dev", i == 3 ? "ALLOCATED_IDLE" : "ATTACHED",
                    new BigDecimal("3.65"), Map.of()));
        }
        for (int i = 1; i <= 10; i++) {
            String env = i <= 3 ? "prod" : i <= 7 ? "staging" : "dev";
            boolean oversized = i >= 4;
            resources.add(new ScannedResource("db-demo-" + i, "DATABASE", "database-" + env + "-" + i,
                    "us-east-1", env, "RUNNING",
                    oversized ? new BigDecimal("450.00") : new BigDecimal("180.00"),
                    Map.of("tier", oversized ? "high" : "standard", "running24x7", true)));
        }
        return resources;
    }
}
