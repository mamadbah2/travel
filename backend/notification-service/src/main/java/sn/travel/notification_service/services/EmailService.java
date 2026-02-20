package sn.travel.notification_service.services;

/**
 * Service interface for sending emails.
 */
public interface EmailService {

    /**
     * Send an HTML email using Thymeleaf templates.
     *
     * @param to           recipient email address
     * @param subject      email subject
     * @param templateName Thymeleaf template name (without extension)
     * @param variables    template variables as key-value pairs
     */
    void sendHtmlEmail(String to, String subject, String templateName, java.util.Map<String, Object> variables);

    /**
     * Send a plain text email.
     *
     * @param to      recipient email address
     * @param subject email subject
     * @param text    plain text body
     */
    void sendSimpleEmail(String to, String subject, String text);
}
