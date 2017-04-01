package com.elixlogic.tifoon.domain.model.core;

import com.elixlogic.tifoon.domain.model.configuration.Validator;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Reporting implements Validator {
    private boolean savePdf;
    private boolean saveHtml;
    private boolean emailPdf;
    private boolean emailHtml;

    @Override
    public void validate() {
    }

    public boolean doReporting() {
        return savePdf || saveHtml || emailPdf || emailHtml;
    }
}
