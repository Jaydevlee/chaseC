package com.example.chaseC.service.serviceImpl;

import com.example.chaseC.dto.MailSendDto;
import com.example.chaseC.service.MailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    @Async
    public void sendStatusUpdateEmail(MailSendDto mailSendDto) {
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true,"utf-8");

            Context context = new Context();
            context.setVariable("newStatus", mailSendDto.getNewStatus());
            context.setVariable("hblNo", mailSendDto.getHblNo());
            String htmlContent = templateEngine.process("email-template", context);
            helper.setTo(mailSendDto.getToEmail());
            helper.setSubject("화물 운송장 상태 업데이트 알림");
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch(Exception e){
            log.error("메일 전송 실패: {}", e.getMessage());
        }
    }

}
