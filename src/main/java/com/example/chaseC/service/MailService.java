package com.example.chaseC.service;

import com.example.chaseC.dto.MailSendDto;

public interface MailService {
    void sendStatusUpdateEmail(MailSendDto mailSendDto);
}
