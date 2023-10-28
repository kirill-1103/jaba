package sovcombank.jabka.userservice.service.interfaces;

import sovcombank.jabka.userservice.model.EmailToken;

public interface MailSenderService {

    void sendVerificationEmail(String userEmail, EmailToken emailToken);

    void sendRecoveryPassword(String userEmail, EmailToken recoveryToken);
}
