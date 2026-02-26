package com.sfsto.controller;

import org.springframework.web.bind.annotation.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final Map<String, PaymentRecord> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @PostMapping
    public Map<String, Object> pay(@RequestBody Map<String, Object> req) {
        String id = (String) req.get("appointmentId");
        double amount = req.get("amount") instanceof Number ? ((Number) req.get("amount")).doubleValue() : 0.0;
        String method = (String) req.getOrDefault("method", "UNKNOWN");
        String payId = "PAY-" + idGen.getAndIncrement();
        PaymentRecord r = new PaymentRecord(payId, id, amount, method, "PAID");
        store.put(payId, r);
        return Map.of("paymentId", payId, "status", r.status);
    }

    static class PaymentRecord {
        String paymentId; String appointmentId; double amount; String method; String status;
        PaymentRecord(String paymentId, String appointmentId, double amount, String method, String status){
            this.paymentId = paymentId; this.appointmentId = appointmentId; this.amount = amount; this.method = method; this.status = status;
        }
        // getters omitted for brevity
    }
}
