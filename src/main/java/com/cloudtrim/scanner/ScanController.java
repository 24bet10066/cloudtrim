package com.cloudtrim.scanner;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/scan")
public class ScanController {
    private final CloudAccountService accountService;

    public ScanController(CloudAccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/demo")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> runDemo() {
        int count = accountService.runDemoScan();
        return Map.of("mode", "DEMO", "resourcesScanned", count);
    }
}
