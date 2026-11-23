package com.cloudtrim.dashboard;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/digest")
public class WeeklyDigestController {
    private final WeeklyDigestService digestService;

    public WeeklyDigestController(WeeklyDigestService digestService) {
        this.digestService = digestService;
    }

    @GetMapping("/weekly")
    public Map<String, String> weekly() {
        return Map.of("digest", digestService.generate());
    }
}
