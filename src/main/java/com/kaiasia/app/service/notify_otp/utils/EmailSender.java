package com.kaiasia.app.service.notify_otp.utils;

import com.kaiasia.app.core.model.ApiResponse;
import com.kaiasia.app.service.notify_otp.model.Trans;
import com.kaiasia.app.service.notify_otp.model.TransResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailSender {
    @Value("${spring.mail.username}")
    private String fromEmail;
    @Autowired
    private JavaMailSender mailSender;
    public boolean send(String LOCATION ,String[] toEmails, Trans trans) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(trans.getAuthenType());
        message.setText(trans.getContent());
        message.setTo(toEmails);
        message.setFrom(fromEmail);

        try{
            mailSender.send(message);
            log.info(LOCATION+"Email sent successfully");
            return true;


        }
        catch (MailException e){

            log.error("{}:{}",LOCATION+"send Email Failed",e);
            return false;
        }
    }
}
