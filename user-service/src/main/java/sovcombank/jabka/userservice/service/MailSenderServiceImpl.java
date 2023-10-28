package sovcombank.jabka.userservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import sovcombank.jabka.userservice.model.EmailToken;
import sovcombank.jabka.userservice.service.interfaces.MailSenderService;

@Service
@PropertySource(value = "classpath:application.yml")
public class MailSenderServiceImpl implements MailSenderService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String emailFrom;
    @Value("${mail-service.activation.uri}")
    private String activationUri;
    @Value("${mail-service.recovery.uri}")
    private String recoveryUrl;

    @Override
    public void sendVerificationEmail(String userEmail, EmailToken emailToken){
        String subject = "Email address confirmation";
        String messageBody = String.format("Для подтверждения Ваше электронной почты и " +
                "активации аккаунта необходимо перейти по ссылке %s", activationUri);
        messageBody = messageBody.replace("{token}", emailToken.getToken());

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(emailFrom);
        mailMessage.setTo(userEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(messageBody);

        mailSender.send(mailMessage);
    }

    @Override
    public void sendRecoveryPassword(String userEmail, EmailToken recoveryToken){
        String subject = "Password Recovery";
        String messageBody = String.format("Для сброса пароля " +
                "Вам необходимо перейти по ссылке %s", recoveryUrl);
        messageBody = messageBody.replace("{token}", recoveryToken.getToken());

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(emailFrom);
        mailMessage.setTo(userEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(messageBody);

        mailSender.send(mailMessage);
    }
}
