package com.elixlogic.tifoon.application.schedulers;

import java.io.IOException;

public interface PortScanScheduler {
    void performScan() throws IOException;
}
