package sn.travel.notification_service.services.implementation;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import sn.travel.notification_service.exceptions.EmailSendingException;
import sn.travel.notification_service.services.EmailService;

import java.util.Map;

/**
 * Implementation of EmailService using Spring Mail and Thymeleaf templates.
 * Sends emails through the configured SMTP server (MailDev in dev).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    private static final String FROM_ADDRESS = "noreply@travel.sn";

    @Override
    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        log.info("Sending HTML email to={}, subject='{}', template='{}'", to, subject, templateName);

        try {
            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(FROM_ADDRESS);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("HTML email sent successfully to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to {}: {}", to, e.getMessage(), e);
            throw new EmailSendingException("Failed to send email to " + to, e);
        }
    }

    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        log.info("Sending simple email to={}, subject='{}'", to, subject);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_ADDRESS);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Simple email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send simple email to {}: {}", to, e.getMessage(), e);
            throw new EmailSendingException("Failed to send email to " + to, e);
        }
    }
}
