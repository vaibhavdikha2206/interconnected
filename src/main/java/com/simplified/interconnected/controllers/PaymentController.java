package com.simplified.interconnected.controllers;

import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.simplified.interconnected.dto.ApiResponse;
import com.simplified.interconnected.dto.PaymentLinkRequestDto;
import com.simplified.interconnected.dto.PaymentLinkResponseDto;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Value("${razorpay.api.key}")
    String apiKey;

    @Value("${razorpay.api.secret}")
    String apiSecret;

    @PostMapping("pay")
    public ResponseEntity<PaymentLinkResponseDto> createPaymentLink(@RequestBody PaymentLinkRequestDto paymentLinkRequestDto,
                                                                    @RequestHeader ("Authorization") String jwt) throws RazorpayException {
        try {
            RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);

            JSONObject paymentLinkRequest = createPaymentLinkRequest(paymentLinkRequestDto);

            PaymentLink paymentLink = razorpayClient.paymentLink.create(paymentLinkRequest);

            PaymentLinkResponseDto paymentLinkResponseDto = new PaymentLinkResponseDto();
            paymentLinkResponseDto.setPayment_link_id(paymentLink.get("id"));
            paymentLinkResponseDto.setPayment_link_url(paymentLink.get("short_url"));

            return new ResponseEntity<PaymentLinkResponseDto>(paymentLinkResponseDto, HttpStatus.CREATED);
        } catch (RazorpayException e) {
            throw new RazorpayException(e.getMessage());
        }
    }

    private static @NotNull JSONObject createPaymentLinkRequest(PaymentLinkRequestDto paymentLinkRequestDto) {
        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount", paymentLinkRequestDto.getAmount());
        paymentLinkRequest.put("currency", "INR");

        JSONObject customer = new JSONObject();
        customer.put("name", paymentLinkRequestDto.getCustomerName());
        customer.put("email", paymentLinkRequestDto.getCustomerEmail());

        JSONObject notify = new JSONObject();
        notify.put("sms", false);
        customer.put("email", false);

        paymentLinkRequest.put("customer", customer);
        paymentLinkRequest.put("notify", notify);
        paymentLinkRequest.put("callback_url", "http://18.212.182.224:8080/api/interconnected/test3");
        paymentLinkRequest.put("callback_method", "get");
        return paymentLinkRequest;
    }

    @PostMapping("redirect")
    public ResponseEntity<ApiResponse> redirect(@RequestBody PaymentLinkRequestDto paymentLinkRequestDto,
                                                @RequestHeader ("Authorization") String jwt) throws RazorpayException {
        try {
            RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);
            Payment payment = razorpayClient.payments.fetch(paymentId);
            if(payment.get("status").equals("captured")) {
                // make changes to order data payment id, payment status completed and status placed.
                // api response with message and status
                return new ResponseEntity<PaymentLinkResponseDto>(paymentLinkResponseDto, HttpStatus.CREATED);
            }

        } catch (RazorpayException e) {
            throw new RazorpayException(e.getMessage());
        }
    }
}
