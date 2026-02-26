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
import com.sfsto.dto.AdminPendingDTO;
import com.sfsto.dto.AdminApplicationDTO;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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
        // 1) station
        Station station = stationRepository.findById(req.stationId).orElse(null);
        if (station == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Unknown station"));
        }
        // 2) vehicle
        Vehicle vehicle = null;
        if (req.vehicleId != null) {
            vehicle = vehicleRepository.findById(req.vehicleId).orElse(null);
        }
        // 3) start time
        String startIso = req.startTime;
        LocalDateTime start;
        try {
            start = LocalDateTime.parse(startIso);
        } catch (Exception ex) {
            start = LocalDateTime.parse(startIso, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        }
        // 4) duration
        int durationMinutes = 60;
        if (req.workOptionIds != null && !req.workOptionIds.isEmpty()) {
            durationMinutes = 0;
            for (Long id : req.workOptionIds) {
                WorkOption w = workOptionRepository.findById(id).orElse(null);
                if (w != null) durationMinutes += w.getDurationMinutes();
            }
        } else if (req.durationMinutes != null) {
            durationMinutes = req.durationMinutes;
        }
        LocalDateTime end = start.plusMinutes(durationMinutes);

        // 5) conflict check
        LocalDateTime finalStart = start;
        boolean conflict = appointmentRepository.findAll().stream()
                .anyMatch(a -> a.getStation() != null
                        && a.getStation().getId().equals(req.stationId)
                        && a.getStartTime() != null
                        && a.getEndTime() != null
                        && finalStart.isBefore(a.getEndTime())
                        && a.getStartTime().isBefore(end));
        if (conflict) {
            return ResponseEntity.status(409).body(Map.of("error", "Time slot conflict"));
        }

        // 6) create appointment
        Appointment appt = new Appointment();
        appt.setStation(station);
        appt.setVehicle(vehicle);
        appt.setStartTime(start);
        appt.setEndTime(end);
        // Маркируем как ожидающее подтверждение менеджера
        appt.setStatus("PENDING");
        appointmentRepository.save(appt);

        // 7) save work items
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

        // 8) response
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

    @GetMapping("/appointments/pending")
    public List<AdminPendingDTO> pending() {
        return appointmentRepository.findAll().stream()
                .filter(a -> "PENDING".equals(a.getStatus()))
                .map(a -> {
                    AdminPendingDTO dto = new AdminPendingDTO();
                    dto.id = a.getId();
                    dto.stationName = a.getStation() != null ? a.getStation().getName() : null;
                    dto.vehicleVin = a.getVehicle() != null ? a.getVehicle().getVin() : null;
                    dto.startTime = a.getStartTime();
                    dto.endTime = a.getEndTime();
                    dto.workOptions = a.getWorkItems() == null ? null : a.getWorkItems().stream()
                            .map(wi -> wi.getWorkOption() != null ? wi.getWorkOption().getName() : null)
                            .filter(java.util.Objects::nonNull)
                            .collect(java.util.stream.Collectors.joining(", "));
                    return dto;
                }).collect(java.util.stream.Collectors.toList());
    }

    @PostMapping("/appointments/{id}/confirm")
    public ResponseEntity<?> confirm(@PathVariable Long id){
        Appointment appt = appointmentRepository.findById(id).orElse(null);
        if (appt == null){ return ResponseEntity.notFound().build(); }
        if (!"PENDING".equals(appt.getStatus())){
            return ResponseEntity.badRequest().body(Map.of("error", "Cannot confirm this appointment"));
        }
        appt.setStatus("CONFIRMED");
        appointmentRepository.save(appt);
        return ResponseEntity.ok(Map.of("id", appt.getId(), "status", appt.getStatus()));
    }

    @PostMapping("/appointments/{id}/deny")
    public ResponseEntity<?> deny(@PathVariable Long id){
        Appointment appt = appointmentRepository.findById(id).orElse(null);
        if (appt == null){ return ResponseEntity.notFound().build(); }
        if ("CANCELLED".equals(appt.getStatus())){
            return ResponseEntity.badRequest().body(Map.of("error", "Already cancelled"));
        }
        appt.setStatus("CANCELLED");
        appointmentRepository.save(appt);
        return ResponseEntity.ok(Map.of("id", appt.getId(), "status", appt.getStatus()));
    }

    @GetMapping("/applications")
    public List<AdminApplicationDTO> applications(){
        return appointmentRepository.findAll().stream().map(a -> {
            AdminApplicationDTO dto = new AdminApplicationDTO();
            Vehicle v = a.getVehicle();
            dto.vehicleMake = v != null ? v.getMake() : null;
            dto.vehicleModel = v != null ? v.getModel() : null;
            dto.licensePlate = v != null ? v.getVin() : null;
            dto.startTime = a.getStartTime();
            dto.endTime = a.getEndTime();
            dto.status = a.getStatus();
            if (a.getWorkItems() != null){
                dto.workNames = a.getWorkItems().stream()
                        .map(wi -> wi.getWorkOption() != null ? wi.getWorkOption().getName() : null)
                        .filter(java.util.Objects::nonNull).collect(java.util.stream.Collectors.joining(", "));
            }
            if (v != null && v.getCustomer() != null){
                // customer data
                dto.customerName = v.getCustomer().getName();
                dto.customerPhone = v.getCustomer().getPhone();
            }
            dto.contactPhone = dto.customerPhone;
            dto.customerComment = a.getCustomerComment();
            return dto;
        }).collect(Collectors.toList());
    }
}
