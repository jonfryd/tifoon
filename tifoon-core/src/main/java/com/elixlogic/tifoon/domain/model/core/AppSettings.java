package com.elixlogic.tifoon.domain.model.core;

import com.elixlogic.tifoon.domain.model.configuration.Validator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

@ConfigurationProperties(prefix = "tifoon")
@Data
@NoArgsConstructor
public class AppSettings implements Validator {
    private int scanRateSeconds;
    private boolean onlySaveReportOnChange;
    private boolean useInitialScanAsBaseline;
    private boolean dynamicBaselineMode;
    private String baselineFilename;

    @Override
    public void validate() {
        Assert.isTrue(scanRateSeconds > 0, "scanRateSeconds must be > 0");
        Assert.hasLength(baselineFilename, "baselineFilename must have length");
    }
}
