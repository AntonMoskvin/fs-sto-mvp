package com.sfsto.controller;

import com.sfsto.dto.AppointmentRequest;
import com.sfsto.model.Appointment;
import com.sfsto.model.Station;
import com.sfsto.model.Vehicle;
import com.sfsto.model.WorkOption;
import com.sfsto.model.AppointmentWork;
import com.sfsto.repository.AppointmentRepository;
import com.sfsto.repository.StationRepository;
import com.sfsto.repository.VehicleRepository;
import com.sfsto.repository.WorkOptionRepository;
import com.sfsto.repository.AppointmentWorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AppointmentController {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private WorkOptionRepository workOptionRepository;
    @Autowired
    private AppointmentWorkRepository appointmentWorkRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    @PostMapping("/appointments")
    public ResponseEntity<?> create(@RequestBody AppointmentRequest req) {
        Station station = stationRepository.findById(req.stationId).orElse(null);
        if (station == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Unknown station"));
        }
        Vehicle vehicle = null;
        if (req.vehicleId != null) {
            vehicle = vehicleRepository.findById(req.vehicleId).orElse(null);
        }
        String startIso = req.startTime;
        LocalDateTime start;
        try {
            start = LocalDateTime.parse(startIso);
        } catch (Exception ex) {
            // Fallback for input like 2026-02-25T19:30
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            start = LocalDateTime.parse(startIso, fmt);
        }
        int totalMinutes = 60;
        if (req.workOptionIds != null && !req.workOptionIds.isEmpty()) {
            totalMinutes = 0;
            for (Long id : req.workOptionIds) {
                WorkOption w = workOptionRepository.findById(id).orElse(null);
                if (w != null) totalMinutes += w.getDurationMinutes();
            }
        } else if (req.durationMinutes != null) {
            totalMinutes = req.durationMinutes;
        }
        LocalDateTime end = start.plusMinutes(totalMinutes);
        // Simple overlap check: ensure no existing appointment for this station overlaps the requested window
        for (Appointment existing : appointmentRepository.findAll()) {
            if (existing.getStation() != null && existing.getStation().getId().equals(station.getId())) {
                if (existing.getEndTime() != null && existing.getStartTime() != null) {
                    if (start.isBefore(existing.getEndTime()) && existing.getStartTime().isBefore(end)) {
                        return ResponseEntity.status(409).body(Map.of("error", "Time slot is already booked"));
                    }
                }
            }
        }
        Appointment appt = new Appointment();
        appt.setStation(station);
        appt.setVehicle(vehicle);
        appt.setStartTime(start);
        appt.setEndTime(end);
        appt.setStatus("SCHEDULED");
        appointmentRepository.save(appt);
        // Persist work items (AppointmentWork)
        if (req.workOptionIds != null) {
            for (Long id : req.workOptionIds) {
                WorkOption w = workOptionRepository.findById(id).orElse(null);
                if (w != null) {
                    AppointmentWork aw = new AppointmentWork();
                    aw.setAppointment(appt);
                    aw.setWorkOption(w);
                    appointmentWorkRepository.save(aw);
                    appt.getWorkItems().add(aw);
                }
            }
            appointmentRepository.save(appt);
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("id", appt.getId());
        resp.put("stationId", station.getId());
        resp.put("vehicleId", vehicle != null ? vehicle.getId() : null);
        resp.put("startTime", appt.getStartTime());
        resp.put("endTime", appt.getEndTime());
        resp.put("status", appt.getStatus());
        resp.put("workOptionIds", req.workOptionIds);
        return ResponseEntity.ok(resp);
    }
}
