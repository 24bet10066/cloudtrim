package com.cloudtrim.scanner;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReadOnlyCloudScanner implements CloudScanner {
    @Override
    public String mode() {
        return "READ_ONLY";
    }

    @Override
    public List<ScannedResource> scan() {
        throw new UnsupportedOperationException(
                "Cloud provider SDK adapter is not configured. Select DEMO mode or configure read-only credentials.");
    }
}
