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
        String pinId = null;
        try {
            pinId = infobipService.sendOtp(phoneNumber);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return pinId;
    }

    // Verify OTP
    @PostMapping("/verify-otp")
    public boolean verifyOtp(@RequestParam String pinId, @RequestParam String otp) {
        try {
            if (infobipService.verifyOTP(pinId, otp)) {
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
