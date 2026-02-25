package com.sfsto.dto;

import java.util.List;

public class AppointmentRequest {
    public Long stationId;
    public Long vehicleId;
    public String startTime; // ISO-8601
    public Integer durationMinutes;
    public List<Long> workOptionIds; // selected works for this appointment
}
