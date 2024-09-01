package dfomenko.service.impl;

import dfomenko.service.EmailService;
import dfomenko.utils.HtmlUtils;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final HtmlUtils htmlUtils;

    @Value("${spring.mail.username}")
    private String emailCinemaAddr;

    @Value("${spring.mail.pseudonym}")
    private String emailCinemaPseudonym;

    @Value("${app.email-output}")
    private String emailOutputTo;


    @Override
    public void sendSimpleMessage(String emailTo,
                                  String subject,
                                  String text) {

        if (emailOutputTo.equals("BOTH") || emailOutputTo.equals("CONSOLE")) {
            outEmailToConsole(emailOutputTo, emailTo, subject, false, text);
        }
        if (emailOutputTo.equals("CONSOLE")) return;

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(emailCinemaAddr);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);

        emailSender.send(mailMessage);
    }


    @Override
    public void sendMessageWithAttachment(String emailTo,
                                          String subject,
                                          String text,
                                          boolean isHtml,
                                          List<ByteArrayDataSource> attachments) throws MessagingException {

        boolean isMultipart = attachments != null;

        if (emailOutputTo.equals("BOTH") || emailOutputTo.equals("CONSOLE")) {
            outEmailToConsole(emailOutputTo, emailTo, subject, isMultipart, text);
        }
        if (emailOutputTo.equals("CONSOLE")) return;

        try {
            MimeMessage emailMessage = emailSender.createMimeMessage();

            MimeMessageHelper emailHelper = new MimeMessageHelper(emailMessage, isMultipart);

            Address address = new InternetAddress(emailCinemaAddr, emailCinemaPseudonym, "utf-8");

            emailMessage.setFrom(address);
            emailHelper.setTo(emailTo);
            emailHelper.setSubject(subject);
            emailHelper.setText(text, isHtml);

            if (isMultipart) {
                for (ByteArrayDataSource entry : attachments) {
                    emailHelper.addInline(entry.getName(), entry);
                }
            }

            emailSender.send(emailMessage);

        } catch (UnsupportedEncodingException e) {
            throw new MessagingException("E-mail address is not valid", e);
        }
    }


    @Override
    public void sendTemplatedMessage(String emailTo,
                                     String subject,
                                     String templateFilePath,
                                     List<ByteArrayDataSource> attachments,
                                     Map<String, Object> parameters) throws MessagingException {

        // Build new mail Html
        String htmlText = htmlUtils.makeHtmlFromTemplate(templateFilePath, parameters);

        this.sendMessageWithAttachment(emailTo, subject, htmlText, true, attachments);

    }


    private void outEmailToConsole(String outputTo,
                                        String emailTo,
                                        String subject,
                                        boolean haveAttachments,
                                        String text) {
        System.out.println("Email:");
        System.out.println("  Email output to: " + outputTo);
        System.out.println("  From email: " + emailCinemaAddr);
        System.out.println("  From pseudonym: " + emailCinemaPseudonym);
        System.out.println("  To: " + emailTo);
        System.out.println("  Subject: " + subject);
        System.out.println("  Attachments present: " + haveAttachments);
        System.out.println("  Message text: \n" + text);
    }

}

