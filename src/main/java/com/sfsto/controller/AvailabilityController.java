package com.sfsto.controller;

import com.sfsto.model.Station;
import com.sfsto.repository.StationRepository;
import com.sfsto.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import com.sfsto.model.Appointment;

@RestController
@RequestMapping("/api/v1")
public class AvailabilityController {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping("/availability")
    public List<Station> availability(@RequestParam(required = false) Long serviceId,
                                    @RequestParam String startTime) {
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = start.plusHours(1); // базовый интервал 1 час

        List<Station> stations = (serviceId != null) ? stationRepository.findByServiceId(serviceId) : stationRepository.findAll();

        // фильтруем станции без конфликтов по времени
        List<Station> available = stations.stream().filter(st -> {
            // собрать все бронирования этой станции
            List<Appointment> appts = appointmentRepository.findAll().stream()
                    .filter(a -> a.getStation() != null && a.getStation().getId().equals(st.getId()))
                    .collect(Collectors.toList());
            boolean conflict = appts.stream().anyMatch(a -> a.getStartTime().isBefore(end) && a.getEndTime().isAfter(start));
            return !conflict;
        }).collect(Collectors.toList());

        return available;
    }
}
