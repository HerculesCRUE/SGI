package org.crue.hercules.sgi.com.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.com.config.SgiConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailSenderService {

  private SgiConfigProperties configProperties;
  private JavaMailSender emailSender;
  private InternetAddress from;
  private boolean copyToSender;

  public EmailSenderService(
      SgiConfigProperties configProperties,
      JavaMailSender emailSender,
      @Value("${spring.mail.properties.mail.from.email:sgi@hercules.com}") String fromEmail,
      @Value("${spring.mail.properties.mail.from.name:SGI}") String fromName,
      @Value("${spring.mail.properties.mail.from.copy:false}") boolean copyToSender) throws AddressException {
    log.debug(
        "EmailService(SgiConfigProperties configProperties, JavaMailSender emailSender, String fromEmail, String fromName, boolean copyToSender) - start");
    this.configProperties = configProperties;
    this.emailSender = emailSender;
    try {
      from = new InternetAddress(fromEmail, fromName);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      from = new InternetAddress(fromEmail);
    }
    this.copyToSender = copyToSender;
    log.debug(
        "EmailService(SgiConfigProperties configProperties, JavaMailSender emailSender, String fromEmail, String fromName, boolean copyToSender) - end");
  }

  public void sendMessage(List<InternetAddress> internetAddresses, String subject, String textBody,
      String htmlBody, List<DataSource> attachments)
      throws MessagingException {
    log.debug(
        "sendMessage(List<InternetAddress> internetAddresses, String subject, String textBody, String htmlBody, List<DataSource> attachments) - start");
    MimeMessage message = emailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);

    helper.setSubject(subject);

    helper.setFrom(from);
    if (copyToSender) {
      helper.addCc(from);
    }

    List<String> fakeEmailRecipients = configProperties.getFakeEmailRecipients();
    if (CollectionUtils.isNotEmpty(fakeEmailRecipients)) {
      log.warn(
          String.format("Sending email to fake recipients (not the real ones): %s", fakeEmailRecipients));
      internetAddresses = fakeEmailRecipients.stream().map(fr -> {
        try {
          return new InternetAddress(fr);
        } catch (AddressException e) {
          throw new IllegalArgumentException(e);
        }
      }).collect(Collectors.toList());
    }
    for (InternetAddress internetAddress : ListUtils.emptyIfNull(internetAddresses)) {
      helper.addBcc(internetAddress);
    }

    // PLAIN TEXT
    if (StringUtils.isNotEmpty(textBody)) {
      // HTML TEXT
      if (StringUtils.isNotEmpty(htmlBody)) {
        helper.setText(textBody, htmlBody);
      } else {
        helper.setText(textBody, false);
      }
      // HTML TEXT
    } else if (StringUtils.isNotEmpty(htmlBody)) {
      helper.setText(htmlBody, true);
    }

    for (DataSource attachment : ListUtils.emptyIfNull(attachments)) {
      helper.addAttachment(attachment.getName(), attachment);
    }

    if (!configProperties.isDisableEmailSend()) {
      emailSender.send(message);
    } else {
      log.warn("Sending email is disabled");
    }

    log.debug(
        "sendMessage(List<InternetAddress> internetAddresses, String subject, String textBody, String htmlBody, List<DataSource> attachments) - end");
  }
}
