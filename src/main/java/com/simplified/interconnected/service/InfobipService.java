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

    public static TfaApi tfaApi;
    public static String pinId;


    public void setupTFA () throws ApiException {
    }
    public void sendOtp(String phoneNumber) throws ApiException {
        ApiClient apiClient = ApiClient.forApiKey(ApiKey.from(API_KEY))
                .withBaseUrl(BaseUrl.from(BASE_URL))
                .build();
        tfaApi = new TfaApi(apiClient);

        TfaApplicationRequest applicationRequest = new TfaApplicationRequest()
                .name("2FA application");

        TfaApplicationResponse tfaApplication = tfaApi
                .createTfaApplication(applicationRequest)
                .execute();

        String appId = tfaApplication.getApplicationId();

        TfaCreateMessageRequest createMessageRequest = new TfaCreateMessageRequest()
                .messageText("Your pin is {{pin}}")
                .pinType(TfaPinType.NUMERIC)
                .pinLength(4);

        TfaMessage tfaMessageTemplate = tfaApi
                .createTfaMessageTemplate(appId, createMessageRequest)
                .execute();

        String messageId = tfaMessageTemplate.getMessageId();

        TfaStartAuthenticationRequest startAuthenticationRequest = new TfaStartAuthenticationRequest()
                .applicationId(appId)
                .messageId(messageId)
                .from("447491163443")
                .to("91" + phoneNumber);

        TfaStartAuthenticationResponse sendCodeResponse = tfaApi
                .sendTfaPinCodeOverSms(startAuthenticationRequest)
                .execute();

        boolean isSuccessful = sendCodeResponse.getSmsStatus().equals("MESSAGE_SENT");
        pinId = sendCodeResponse.getPinId();
    }

    public boolean verifyOTP(String pin) throws Exception{
        TfaVerifyPinRequest verifyPinRequest = new TfaVerifyPinRequest()
                .pin(pin);

        TfaVerifyPinResponse verifyResponse = tfaApi
                .verifyTfaPhoneNumber(pinId, verifyPinRequest)
                .execute();

        return verifyResponse.getVerified();
    }
}

