package com.elixlogic.tifoon.domain.service.reporting.impl;

import com.elixlogic.tifoon.domain.model.core.Mail;
import com.elixlogic.tifoon.domain.service.reporting.ReportEmailSenderService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class ReportEmailSenderServiceImpl implements ReportEmailSenderService {
    private final JavaMailSender mailSender;

    @Autowired
    public ReportEmailSenderServiceImpl(final JavaMailSender _mailSender) {
        mailSender = _mailSender;
    }

    @Override
    public void sendEmail(@NonNull final Mail _mail,
                          @NonNull final String _body,
                          @Nullable final String _attachmentFilename,
                          @Nullable final byte[] _pdfAttachment) {
        try {
            // Prepare message using a Spring helper
            final MimeMessage mimeMessage = mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, (_pdfAttachment != null), StandardCharsets.UTF_8.name());
            message.setSubject("Tifoon Scan Report");
            message.setFrom(_mail.getSender());
            message.setTo(_mail.getRecipient());

            message.setText(_body, true);

            if (_attachmentFilename != null && _pdfAttachment != null) {
                message.addAttachment(_attachmentFilename, new ByteArrayResource(_pdfAttachment));
            }

            // Send mail
            mailSender.send(mimeMessage);
        } catch (MessagingException _e) {
            log.error("Failed to send e-mail", _e);
        }
    }
}
