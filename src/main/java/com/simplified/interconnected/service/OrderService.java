package com.simplified.interconnected.service;

import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.simplified.interconnected.dto.*;
import com.simplified.interconnected.models.*;
import com.simplified.interconnected.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Transactional
public class OrderService {
    // reference: https://youtu.be/EEwngSnv8LU?si=81UHU4rCf09NI3GU
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ServiceRepository serviceRepository;
    @Autowired
    ExpertRepository expertRepository;

    private static final String currency = "INR";

    @Autowired
    ExpertService expertService;

    @Value("${razorpay.api.key}")
    String apiKey;

    @Value("${razorpay.api.secret}")
    String apiSecret;

    @PostMapping("pay")
    @ResponseBody
    public OrderResponseDto createRazorpayOrder(OrderRequestDto orderRequestDto) throws Exception {
        if(!expertService.validateOrderRequest(orderRequestDto.getExpertId(), orderRequestDto.getServiceTimeslot()))
        {
            return null;
        }
        ServiceEntity service = serviceRepository.findById(orderRequestDto.getServiceId())
                .orElseThrow(() -> new EntityNotFoundException("Service not found for id: " + orderRequestDto.getServiceId()));

        ExpertEntity expert = expertRepository.findById(orderRequestDto.getExpertId())
                .orElseThrow(() -> new EntityNotFoundException("Expert not found for id: " + orderRequestDto.getExpertId()));

        RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);

        JSONObject rpOrderRequest = createRazorpayOrderRequest(orderRequestDto, service.getPrice());

        Order rpOrder = razorpayClient.orders.create(rpOrderRequest);

        // Add code for error/failure case

        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setRazorpayOrderId(rpOrder.get("id"));
        orderResponseDto.setCurrency(currency);
        orderResponseDto.setAmount(service.getPrice());

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

        return orderResponseDto;
    }

    private static @NotNull JSONObject createRazorpayOrderRequest(OrderRequestDto orderRequestDto, double price) {
        JSONObject rpOrderRequest = new JSONObject();
        rpOrderRequest.put("amount", price*100);
        rpOrderRequest.put("currency", currency);

        JSONObject customerInfo = new JSONObject();
        customerInfo.put("customer_name", orderRequestDto.getCustomerName());
        customerInfo.put("customer_email", orderRequestDto.getCustomerEmail());

        rpOrderRequest.put("notes", customerInfo);
        return rpOrderRequest;
    }

    public ApiResponse completeOrder(String paymentId) {
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
                return new ApiResponse("Order placed", true);
            }
        } catch (Exception e) {
            return new ApiResponse(e.getMessage(), false);
        }
        return null;
    }
}
