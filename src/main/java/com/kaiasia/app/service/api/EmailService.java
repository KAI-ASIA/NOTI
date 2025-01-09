package com.kaiasia.app.service.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaiasia.app.core.model.*;
import com.kaiasia.app.core.utils.ApiConstant;
import com.kaiasia.app.core.utils.GetErrorUtils;
import com.kaiasia.app.register.KaiMethod;
import com.kaiasia.app.register.KaiService;
import com.kaiasia.app.register.Register;
import com.kaiasia.app.service.model.Trans;
import com.kaiasia.app.service.model.TransResponse;
import com.kaiasia.app.service.utils.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.validation.ConstraintViolation;
import java.util.*;

@KaiService
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GetErrorUtils getErrorUtils;

    @Value("${spring.mail.username}")
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

        error= ValidatorUtils.validate(trans,getErrorUtils);

        if(!ApiError.OK_CODE.equals(error.getCode())&& !ApiError.OK_DESC.equals(error.getDesc())){

            log.error(Location);

            apiResponse.setError(error);
            return apiResponse;
        }

        String[] toEmails= trans.getTo().split(",");

        // gửi email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(trans.getAuthenType());
        message.setText(trans.getContent());
        message.setTo(toEmails);
        message.setFrom(fromEmail);


        try{
            mailSender.send(message);

            log.info(Location+"Email sent successfully");

            TransResponse transResponse=new TransResponse();
            transResponse.setContent("send email successfully");
            body.put("transaction",transResponse);
            apiResponse.setBody(body);


        }
        catch (MailException e){

            log.error("{}:{}",Location+"send Email Failed",e);
            error=getErrorUtils.getError("");
            body.put("error",error);
            body.put("status","FAILED");
            return apiResponse;
        }

        return apiResponse;
    }
}
