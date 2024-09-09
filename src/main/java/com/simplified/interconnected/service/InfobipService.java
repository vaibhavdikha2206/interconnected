package com.simplified.interconnected.service;

import com.infobip.ApiClient;
import com.infobip.ApiException;
import com.infobip.ApiKey;
import com.infobip.BaseUrl;
import com.infobip.api.TfaApi;
import com.infobip.model.*;
import org.springframework.stereotype.Service;

@Service
public class InfobipService {

    private static final String BASE_URL = "https://518v9d.api.infobip.com"; // Replace with your Infobip Base URL
    private static final String API_KEY = "984970dd35ab4014a8edb1514c8f1fe3-02c29ca0-500a-459d-9bb7-ae0bc6598c6c"; // Replace with your Infobip API Key

    private final TfaApi tfaApi;
    private final String appId;
    private final String messageId;


    public InfobipService() throws ApiException {
        // Initialize ApiClient and TfaApi once
        // these are class fields initialized once
        ApiClient apiClient = ApiClient.forApiKey(ApiKey.from(API_KEY))
                .withBaseUrl(BaseUrl.from(BASE_URL))
                .build();
        tfaApi = new TfaApi(apiClient);

        // Create the TFA application and message template once
        TfaApplicationRequest applicationRequest = new TfaApplicationRequest()
                .name("2FA application");
        TfaApplicationResponse tfaApplication = tfaApi.createTfaApplication(applicationRequest)
                .execute();
        appId = tfaApplication.getApplicationId();

        TfaCreateMessageRequest createMessageRequest = new TfaCreateMessageRequest()
                .messageText("Your pin is {{pin}}")
                .pinType(TfaPinType.NUMERIC)
                .pinLength(4);
        TfaMessage tfaMessageTemplate = tfaApi.createTfaMessageTemplate(appId, createMessageRequest)
                .execute();
        messageId = tfaMessageTemplate.getMessageId();
    }

    // Method to send OTP
    public String sendOtp(String phoneNumber) throws ApiException {
        // Create the authentication request
        TfaStartAuthenticationRequest startAuthenticationRequest = new TfaStartAuthenticationRequest()
                .applicationId(appId)
                .messageId(messageId)
                .from("447491163443")
                .to("91" + phoneNumber);
        // Send the OTP
        TfaStartAuthenticationResponse sendCodeResponse = tfaApi.sendTfaPinCodeOverSms(startAuthenticationRequest)
                .execute();

        // Check status and handle response
        boolean isSuccessful = "MESSAGE_SENT".equals(sendCodeResponse.getSmsStatus());

        // Handle success or failure accordingly
        if (isSuccessful) {
            System.out.println("OTP sent successfully to " + phoneNumber);
        } else {
            System.err.println("Failed to send OTP.");
        }
        return sendCodeResponse.getPinId();
    }

    public boolean verifyOTP(String pinId, String otp) throws Exception{
        TfaVerifyPinRequest verifyPinRequest = new TfaVerifyPinRequest().pin(otp);

        TfaVerifyPinResponse verifyResponse = tfaApi
                .verifyTfaPhoneNumber(pinId, verifyPinRequest)
                .execute();

        return verifyResponse.getVerified();
    }
}

