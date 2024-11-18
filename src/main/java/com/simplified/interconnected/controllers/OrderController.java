package com.simplified.interconnected.controllers;

import com.simplified.interconnected.dto.ApiResponse;
import com.simplified.interconnected.dto.OrderRequestDto;
import com.simplified.interconnected.dto.OrderResponseDto;
import com.simplified.interconnected.repository.ExpertRepository;
import com.simplified.interconnected.repository.OrderRepository;
import com.simplified.interconnected.repository.ServiceRepository;
import com.simplified.interconnected.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private static final String currency = "INR";

    @Value("${razorpay.api.key}")
    String apiKey;

    @Value("${razorpay.api.secret}")
    String apiSecret;

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("pay")
    @ResponseBody
    public ResponseEntity<OrderResponseDto> createRazorpayOrder(@RequestBody OrderRequestDto orderRequestDto) {
        try {
            OrderResponseDto response = orderService.createRazorpayOrder(orderRequestDto);
            if(response == null){
                return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
            }
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("confirm")
    @ResponseBody
    public ResponseEntity<ApiResponse> completeOrder(@RequestParam(name="razorpay_payment_id") String paymentId) {
        ApiResponse apiResponse = orderService.completeOrder(paymentId);
        if (apiResponse == null) {
            return null;
        }
        return new ResponseEntity<>(apiResponse, apiResponse.isStatus() ? HttpStatus.CREATED: HttpStatus.BAD_REQUEST);
    }
}
