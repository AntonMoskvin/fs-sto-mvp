package com.sfsto.controller;

import com.sfsto.dto.AdminNotificationDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AdminNotificationsController {
    @GetMapping("/notifications")
    public List<AdminNotificationDTO> list(){
        AdminNotificationDTO n1 = new AdminNotificationDTO(); n1.id = 1L; n1.appointmentId = 2001L; n1.message = "Новая заявка на бронирование"; n1.type = "NEW"; n1.status = "NEW"; n1.createdAt = LocalDateTime.now().minusHours(3);
        AdminNotificationDTO n2 = new AdminNotificationDTO(); n2.id = 2L; n2.appointmentId = 2002L; n2.message = "Заявка на бронирование ожидает подтверждения"; n2.type = "UPDATE"; n2.status = "NEW"; n2.createdAt = LocalDateTime.now().minusHours(2);
        AdminNotificationDTO n3 = new AdminNotificationDTO(); n3.id = 3L; n3.appointmentId = 2003L; n3.message = "Новая заявка на бронирование"; n3.type = "NEW"; n3.status = "NEW"; n3.createdAt = LocalDateTime.now().minusHours(1);
        return Arrays.asList(n1, n2, n3);
    }
}
