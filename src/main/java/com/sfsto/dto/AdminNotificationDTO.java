package com.sfsto.dto;

import java.time.LocalDateTime;

public class AdminNotificationDTO {
    public Long id;
    public Long appointmentId;
    public String message;
    public String type; // e.g., NEW, UPDATE
    public String status; // NEW, SEEN, DONE
    public LocalDateTime createdAt;
}
