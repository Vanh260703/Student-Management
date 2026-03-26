package com.example.quan_ly_sinh_vien_v2.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    JavaMailSender mailSender;

    @Async
    public void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("student-mangement@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);


        mailSender.send(message);

        System.out.println("Gửi mail thành công");
    }
}
