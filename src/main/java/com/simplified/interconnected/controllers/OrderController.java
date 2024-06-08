package com.simplified.interconnected.controllers;

import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.simplified.interconnected.dto.ApiResponse;
import com.simplified.interconnected.dto.OrderCreateRequestDTO;
import com.simplified.interconnected.dto.PaymentLinkRequestDto;
import com.simplified.interconnected.dto.PaymentLinkResponseDto;
import com.simplified.interconnected.models.ExpertEntity;
import com.simplified.interconnected.models.OrderEntity;
import com.simplified.interconnected.repository.ExpertsRepository;
import com.simplified.interconnected.repository.OrderRepository;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    // reference: https://youtu.be/EEwngSnv8LU?si=81UHU4rCf09NI3GU
    private final OrderRepository orderRepository;
    private final ExpertsRepository expertsRepository;

    @Value("${razorpay.api.key}")
    String apiKey;

    @Value("${razorpay.api.secret}")
    String apiSecret;

    @Autowired
    public OrderController(OrderRepository orderRepository, ExpertsRepository expertsRepository) {
        this.orderRepository = orderRepository;
        this.expertsRepository = expertsRepository;
    }

    @GetMapping("expertServices")
    public ResponseEntity<ExpertEntity> getExpertServices(@PathVariable Long expertId) {
        try {
            ExpertEntity expert = expertsRepository.getById(expertId);
            return new ResponseEntity<>(expert, HttpStatus.OK);
        } catch (Exception e)
        {
            return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
        @PostMapping("pay")
    public ResponseEntity<PaymentLinkResponseDto> createPaymentLink(@RequestBody PaymentLinkRequestDto paymentLinkRequestDto,
                                                                    @RequestHeader ("Authorization") String jwt) throws RazorpayException {
        try {
            RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);

            JSONObject paymentLinkRequest = createPaymentLinkRequest(paymentLinkRequestDto);

            PaymentLink paymentLink = razorpayClient.paymentLink.create(paymentLinkRequest);

            PaymentLinkResponseDto paymentLinkResponseDto = new PaymentLinkResponseDto();
            paymentLinkResponseDto.setPaymentLinkId(paymentLink.get("id"));
            paymentLinkResponseDto.setPaymentLinkUrl(paymentLink.get("short_url"));

            // get product details from product repo
            // use that data to create the order entity
            // Save new order
            OrderEntity order = orderRepository.getById(paymentLinkRequestDto.getOrderId());
            order.setPaymentId(paymentLinkResponseDto.getPaymentLinkId());
            order.setPaymentStatus("PENDING");
            order.setOrderStatus("PENDING");
            orderRepository.save(order);

            return new ResponseEntity<>(paymentLinkResponseDto, HttpStatus.CREATED);
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
    public ResponseEntity<ApiResponse> redirect(@RequestParam(name="payment_id") String paymentId,
                                                @RequestHeader ("Authorization") String jwt) throws RazorpayException {
        try {
            RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);
            Payment payment = razorpayClient.payments.fetch(paymentId);
            if(payment.get("status").equals("captured")) {
                // Save payment id for order
                OrderEntity order = orderRepository.findByPaymentId(paymentId).get();
                order.setPaymentStatus("COMPLETED");
                order.setOrderStatus("PLACED");
                orderRepository.save(order);
                return new ResponseEntity<>(new ApiResponse("Order placed", true), HttpStatus.CREATED);
            }
        } catch (RazorpayException e) {
            throw new RazorpayException(e.getMessage());
        }
        return null;
    }
}
