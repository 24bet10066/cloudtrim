package com.cloudtrim.scanner;

import java.util.List;

public interface CloudScanner {
    String mode();
    List<ScannedResource> scan();
}
