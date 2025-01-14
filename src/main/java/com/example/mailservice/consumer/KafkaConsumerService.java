package com.example.mailservice.consumer;

import com.example.mailservice.service.MailService;
import org.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final MailService mailService;

    public KafkaConsumerService(MailService mailService) {
        this.mailService = mailService;
    }

    @KafkaListener(topics = "otp-topic", groupId = "otp-group")
    public void consumeMessage(String message) {
        try {
            // Chuyển đổi chuỗi JSON thành JSONObject
            JSONObject jsonObject = new JSONObject(message);

            // Lấy giá trị hoặc giá trị mặc định nếu không có key
            String email = jsonObject.optString("email", "no-reply@example.com");
            String subject = jsonObject.optString("subject", "Default Subject");
            String content = jsonObject.optString("content", "No content.");

            // Gửi email
            mailService.sendEmail(email, subject, content);
            System.out.println("Email sent to: " + email);
        } catch (Exception e) {
            System.err.println("Error processing Kafka message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
