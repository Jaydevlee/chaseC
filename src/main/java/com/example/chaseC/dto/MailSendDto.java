package com.example.chaseC.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class MailSendDto {
    private String toEmail;
    private String hblNo;
    private String newStatus;
}
