package com.simplified.interconnected.controllers;

import com.simplified.interconnected.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private OtpStorageService otpStorageService;

    @PostMapping("/send")
    public String sendOtp(@RequestParam String phoneNumber) {
        String otp = otpService.generateOtp();
        otpService.sendOtp(phoneNumber, otp);
        otpStorageService.storeOtp(phoneNumber, otp);
        return "OTP sent successfully to " + phoneNumber;
    }

    @PostMapping("/verify")
    public String verifyOtp(@RequestParam String phoneNumber, @RequestParam String otp) {
        String storedOtp = otpStorageService.getOtp(phoneNumber);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStorageService.removeOtp(phoneNumber);
            return "OTP verified successfully!";
        } else {
            return "Invalid OTP!";
        }
    }
}
