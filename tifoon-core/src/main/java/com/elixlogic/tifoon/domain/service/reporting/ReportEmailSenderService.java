package com.elixlogic.tifoon.domain.service.reporting;

import com.elixlogic.tifoon.domain.model.core.Mail;

import javax.annotation.Nullable;

public interface ReportEmailSenderService {
    void sendEmail(Mail _mail,
                   String _body,
                   @Nullable String _attachmentFilename,
                   @Nullable byte[] _pdfAttachment);
}
