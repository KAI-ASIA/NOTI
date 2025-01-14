package com.kaiasia.app.service.notify_otp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaiasia.app.core.model.*;
import com.kaiasia.app.core.utils.GetErrorUtils;
import com.kaiasia.app.register.KaiMethod;
import com.kaiasia.app.register.KaiService;
import com.kaiasia.app.register.Register;
import com.kaiasia.app.service.notify_otp.model.EmailValid;
import com.kaiasia.app.service.notify_otp.model.Trans;
import com.kaiasia.app.service.notify_otp.model.TransResponse;
import com.kaiasia.app.service.notify_otp.utils.EmailSender;
import com.kaiasia.app.service.notify_otp.utils.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.validation.constraints.Email;
import java.util.*;

@KaiService
@Slf4j
public class EmailService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GetErrorUtils getErrorUtils;

    @Autowired
    private EmailSender emailSender;

    @Value("${spring.mail.username}")
    @Email(message = "Invalid fromEmail address")
    private String fromEmail;

    @KaiMethod(name = "EmailService", type = Register.VALIDATE)
    public ApiError validate(ApiRequest req) throws Exception {


        if(req.getBody()==null){
            return getErrorUtils.getError("706",new String []{"#asd"});
        }

        LinkedHashMap<String,Object> transaction=(LinkedHashMap) req.getBody().get("transaction");

        Trans trans=objectMapper.convertValue(transaction,Trans.class);

        if(StringUtils.isBlank(trans.getTo())){
            return getErrorUtils.getError("706",new String []{"#to Email"});
        }


        return new ApiError(ApiError.OK_CODE,ApiError.OK_DESC);
    }

    @KaiMethod(name = "EmailService")
    public ApiResponse process(ApiRequest req) throws Exception {

        LinkedHashMap<String,Object> transaction=(LinkedHashMap) req.getBody().get("transaction");

        String Location="send email"+ req.getHeader().getApi()+System.currentTimeMillis();

        ApiResponse apiResponse=new ApiResponse();
        apiResponse.setHeader(req.getHeader());
        ApiBody body=new ApiBody();
        ApiError error= new ApiError();

        Trans trans=objectMapper.convertValue(transaction,Trans.class);

        String[] toEmails= trans.getTo().split(",");
        for (String email:toEmails) {
            EmailValid emailValid=new EmailValid();
            emailValid.setEmail(email);
            error= ValidatorUtils.validate(emailValid,getErrorUtils);

            if(!ApiError.OK_CODE.equals(error.getCode())&& !ApiError.OK_DESC.equals(error.getDesc())){

                log.error(Location);

                apiResponse.setError(error);
                return apiResponse;
            }
        }

        // gửi email
        boolean isEmailSent=emailSender.send(Location,toEmails,trans);
        if(isEmailSent){
            TransResponse transResponse=new TransResponse();
            transResponse.setContent("sent email successfully");
            body.put("trans",transResponse);
            apiResponse.setBody(body);
        }
        else {
            error=getErrorUtils.getError("");
            apiResponse.setError(error);
        }

        return apiResponse;
    }
}
