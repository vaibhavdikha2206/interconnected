package com.simplified.interconnected.controllers;

import com.razorpay.*;
import com.simplified.interconnected.dto.ApiResponse;
import com.simplified.interconnected.dto.OrderRequestDto;
import com.simplified.interconnected.dto.OrderResponseDto;
import com.simplified.interconnected.models.ExpertEntity;
import com.simplified.interconnected.models.OrderEntity;
import com.simplified.interconnected.models.ServiceEntity;
import com.simplified.interconnected.repository.ExpertRepository;
import com.simplified.interconnected.repository.OrderRepository;
import com.simplified.interconnected.repository.ServiceRepository;
import com.simplified.interconnected.service.ExpertService;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    // reference: https://youtu.be/EEwngSnv8LU?si=81UHU4rCf09NI3GU
    private final OrderRepository orderRepository;
    private final ServiceRepository serviceRepository;
    private final ExpertRepository expertRepository;

    private static final String currency = "INR";

    @Autowired
    private ExpertService expertService;

    @Value("${razorpay.api.key}")
    String apiKey;

    @Value("${razorpay.api.secret}")
    String apiSecret;

    @Autowired
    public OrderController(OrderRepository orderRepository, ExpertRepository expertRepository, ServiceRepository serviceRepository) {
        this.orderRepository = orderRepository;
        this.serviceRepository = serviceRepository;
        this.expertRepository = expertRepository;
    }

    @PostMapping("pay")
    @ResponseBody
    public ResponseEntity<OrderResponseDto> createRazorpayOrder(@RequestBody OrderRequestDto orderRequestDto) throws RazorpayException {
        if(!expertService.validateOrderRequest(orderRequestDto.getExpertId(), orderRequestDto.getServiceTimeslot()))
        {
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }
        try {
            ServiceEntity service = serviceRepository.getById(orderRequestDto.getServiceId());
            ExpertEntity expert = expertRepository.getById(orderRequestDto.getExpertId());
            RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);

            JSONObject rpOrderRequest = createRazorpayOrderRequest(orderRequestDto, service.getPrice());

            Order rpOrder = razorpayClient.orders.create(rpOrderRequest);

            // Add code for error/failure case

            OrderResponseDto orderResponseDto = new OrderResponseDto();
            orderResponseDto.setRazorpayOrderId(rpOrder.get("id"));

            OrderEntity order = new OrderEntity();
            order.setService(service);
            order.setExpert(expert);
            order.setServiceTimeslot(orderRequestDto.getServiceTimeslot()); // Verify if timeslot is valid
            order.setCost(service.getPrice());
            order.setCurrency(currency);
            order.setCustomerEmail(orderRequestDto.getCustomerEmail());
            order.setRazorpayOrderId(orderResponseDto.getRazorpayOrderId());
            order.setPaymentStatus("PENDING");
            order.setOrderStatus("PENDING");
            order.setOrderTimestamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
            orderRepository.save(order);

            return new ResponseEntity<>(orderResponseDto, HttpStatus.CREATED);
        } catch (RazorpayException e) {
            throw new RazorpayException(e.getMessage());
        }
    }

    private static @NotNull JSONObject createRazorpayOrderRequest(OrderRequestDto orderRequestDto, double price) {
        JSONObject rpOrderRequest = new JSONObject();
        rpOrderRequest.put("amount", price);
        rpOrderRequest.put("currency", currency);

        JSONObject customerInfo = new JSONObject();
        customerInfo.put("customer_name", orderRequestDto.getCustomerName());
        customerInfo.put("customer_email", orderRequestDto.getCustomerEmail());

        rpOrderRequest.put("notes", customerInfo);
        return rpOrderRequest;
    }

    @PostMapping("redirect")
    @ResponseBody
    public ResponseEntity<ApiResponse> redirect(@RequestParam(name="razorpay_payment_id") String paymentId)
            throws Exception {
        try {
            RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);
            Payment payment = razorpayClient.payments.fetch(paymentId);
            if(payment.get("status").equals("captured")) {
                // Save payment id for order
                OrderEntity order = orderRepository.findByRazorpayOrderId(payment.get("order_id"))
                        .orElseThrow(() -> new IllegalStateException("No order present"));
                order.setRazorpayPaymentId(paymentId);
                order.setPaymentStatus("COMPLETED");
                order.setOrderStatus("PLACED");
                orderRepository.save(order);
                return new ResponseEntity<>(new ApiResponse("Order placed", true), HttpStatus.CREATED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}
