package com.ufc.almoxarifado.config;

import freemarker.template.Configuration;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final Configuration freemarkerConfig;

    @Async
    public void sendEmail(String to, String subject, String templateName, Map<String, Object> model) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String html = FreeMarkerTemplateUtils.processTemplateIntoString(
                    freemarkerConfig.getTemplate(templateName), model);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email to {}", to, e);
        }
    }
}

