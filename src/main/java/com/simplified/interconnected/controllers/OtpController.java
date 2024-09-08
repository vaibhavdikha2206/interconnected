package com.simplified.interconnected.controllers;

import com.simplified.interconnected.service.InfobipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class OtpController {

    @Autowired
    private InfobipService infobipService;

    // Generate and send OTP
    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String phoneNumber) {
        try {
            infobipService.sendOtp(phoneNumber);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "OTP sent successfully to " + phoneNumber;
    }

    // Verify OTP
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String phoneNumber, @RequestParam String otp) {
        try {
            if (infobipService.verifyOTP(otp)) {
                return "OTP verified successfully!";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "Invalid OTP!";
    }
}
