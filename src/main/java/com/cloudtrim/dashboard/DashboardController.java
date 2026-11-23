package com.cloudtrim.dashboard;

import com.cloudtrim.common.TenantContext;
import com.cloudtrim.waste.WasteFinding;
import com.cloudtrim.waste.WasteFindingRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class DashboardController {
    private final DashboardService dashboardService;
    private final WasteFindingRepository findingRepository;

    public DashboardController(DashboardService dashboardService, WasteFindingRepository findingRepository) {
        this.dashboardService = dashboardService;
        this.findingRepository = findingRepository;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return dashboardService.summary();
    }

    @GetMapping("/findings")
    public List<FindingResponse> findings(@RequestParam(required = false) WasteFinding.Status status) {
        UUID tenantId = TenantContext.getRequired();
        List<WasteFinding> findings = status == null ? findingRepository.findByTenantId(tenantId)
                : findingRepository.findByTenantIdAndStatus(tenantId, status);
        return findings.stream().map(FindingResponse::from).toList();
    }

    @PostMapping("/findings/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public FindingResponse resolve(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getRequired();
        WasteFinding finding = findingRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND));
        finding.resolve(WasteFinding.Status.RESOLVED);
        return FindingResponse.from(findingRepository.save(finding));
    }

    @PostMapping("/findings/{id}/ignore")
    @PreAuthorize("hasRole('ADMIN')")
    public FindingResponse ignore(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getRequired();
        WasteFinding finding = findingRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND));
        finding.resolve(WasteFinding.Status.IGNORED);
        return FindingResponse.from(findingRepository.save(finding));
    }

    @GetMapping(value = "/export/findings.csv", produces = "text/csv")
    public ResponseEntity<byte[]> export() {
        UUID tenantId = TenantContext.getRequired();
        StringBuilder csv = new StringBuilder("finding_id,rule_code,title,status,severity,monthly_waste_usd,detected_at\n");
        for (WasteFinding finding : findingRepository.findByTenantId(tenantId)) {
            csv.append(finding.getId()).append(',')
                    .append(finding.getRuleCode()).append(',')
                    .append("\"").append(finding.getTitle().replace("\"", "\"\"")).append("\",")
                    .append(finding.getStatus()).append(',')
                    .append(finding.getSeverity()).append(',')
                    .append(finding.getMonthlyWasteUsd()).append(',')
                    .append(finding.getDetectedAt()).append('\n');
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=findings.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }
}
