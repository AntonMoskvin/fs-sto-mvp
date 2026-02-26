package com.sfsto.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AdminApplicationDTO {
    public Long id;
    public String vehicleMake;
    public String vehicleModel;
    public String licensePlate; // гос номер / VIN
    public LocalDateTime startTime;
    public LocalDateTime endTime;
    public String status; // PENDING, CONFIRMED, CANCELLED
    public String workNames; // comma separated list of work option names
    public String contactPhone;
    public String customerComment; // comment from client to admin
    // Additional customer info
    public String customerName;
    public String customerPhone;
}
