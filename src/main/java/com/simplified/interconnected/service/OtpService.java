package com.simplified.interconnected.service;

import com.plivo.api.Plivo;
import com.plivo.api.models.message.Message;
import com.plivo.api.models.message.MessageCreateResponse;
import com.plivo.api.models.message.MessageCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OtpService {

    @Value("${plivo.authId}")
    private String authId;

    @Value("${plivo.authToken}")
    private String authToken;

    @Value("${plivo.phoneNumber}")
    private String plivoPhoneNumber;

    public OtpService() {
        Plivo.init(authId, authToken);
    }

    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void sendOtp(String phoneNumber, String otp) {
        try {
            MessageCreator messageCreator = Message.creator(
                    plivoPhoneNumber,
                    phoneNumber,
                    "Your OTP is: " + otp);
            MessageCreateResponse response = messageCreator.create();
            System.out.println("OTP sent successfully, Message UUID: " + response.getMessageUuid());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send OTP.");
        }
    }
}
