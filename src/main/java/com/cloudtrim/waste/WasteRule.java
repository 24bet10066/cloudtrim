package com.cloudtrim.waste;

import com.cloudtrim.cloud.CloudAccount;
import com.cloudtrim.cloud.CloudResource;

import java.util.Optional;

public interface WasteRule {
    String code();
    Optional<WasteFindingCandidate> evaluate(CloudAccount account, CloudResource resource);
}
