package it.flipb.theapp.domain.model.masterplan;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(locations = {"classpath:masterplan.yml", "classpath:config/masterplan.yml", "file:masterplan.yml", "file:config/masterplan.yml"}, prefix = "masterplan")
@Data
@NoArgsConstructor
public class MasterPlan {
    @NonNull
    private Scanner scanner;
    @NonNull
    private String commandExecutor;
    @NonNull
    private String ioFormat;
}