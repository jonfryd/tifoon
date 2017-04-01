package com.elixlogic.tifoon.domain.model.core;

import com.elixlogic.tifoon.domain.model.configuration.Validator;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.modelmapper.internal.util.Assert;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Data
@NoArgsConstructor
public class Mail implements Validator {
    private String sender;
    private String recipient;

    @Override
    public void validate() {
        Assert.isTrue(isValidEmailAddress(sender), "Sender is not a valid e-mail address: " + sender);;
        Assert.isTrue(isValidEmailAddress(recipient), "Recipient is not a valid e-mail address: " + recipient);;
    }

    // http://stackoverflow.com/questions/624581/what-is-the-best-java-email-address-validation-method
    private static boolean isValidEmailAddress(@NonNull final String _email) {
        try {
            new InternetAddress(_email).validate();
            return true;
        } catch (AddressException ex) {
            return false;
        }
    }
}
