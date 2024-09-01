package dfomenko.service;


import jakarta.mail.MessagingException;
import jakarta.mail.util.ByteArrayDataSource;

import java.util.List;
import java.util.Map;


public interface EmailService {

    void sendSimpleMessage(String emailTo,
                           String subject,
                           String text);

    void sendMessageWithAttachment(String emailTo,
                                   String subject,
                                   String text,
                                   boolean isHtml,
                                   List<ByteArrayDataSource> attachments) throws MessagingException;

    void sendTemplatedMessage(String emailTo,
                              String subject,
                              String templateFilePath,
                              List<ByteArrayDataSource> attachments,
                              Map<String, Object> parameters) throws MessagingException;
}