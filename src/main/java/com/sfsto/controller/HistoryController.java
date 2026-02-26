package com.sfsto.controller;

import com.sfsto.dto.HistoryDTO;
import com.sfsto.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/history")
public class HistoryController {
    @Autowired
    private HistoryRepository historyRepository;

    @GetMapping("")
    public List<HistoryDTO> list(@org.springframework.web.bind.annotation.RequestParam(value = "from", required = false) String from,
                                 @org.springframework.web.bind.annotation.RequestParam(value = "to", required = false) String to) {
        java.util.List<com.sfsto.model.HistoryEntry> all = historyRepository.findAll();
        java.util.stream.Stream<com.sfsto.model.HistoryEntry> stream = all.stream();
        if (from != null || to != null) {
            java.time.LocalDateTime fromDT = null;
            java.time.LocalDateTime toDT = null;
            try {
                if (from != null) fromDT = java.time.LocalDateTime.parse(from);
            } catch (Exception e) {
                try { fromDT = java.time.LocalDate.parse(from).atStartOfDay(); } catch (Exception ex) { /* ignore */ }
            }
            try {
                if (to != null) toDT = java.time.LocalDateTime.parse(to);
            } catch (Exception e) {
                try { toDT = java.time.LocalDate.parse(to).atTime(23,59,59); } catch (Exception ex) { /* ignore */ }
            }
            final java.time.LocalDateTime f = fromDT;
            final java.time.LocalDateTime t = toDT;
            stream = stream.filter(h -> {
                java.time.LocalDateTime ts = h.getTimestamp();
                boolean after = (f == null) ? true : !ts.isBefore(f);
                boolean before = (t == null) ? true : !ts.isAfter(t);
                return after && before;
            });
            // Do not limit to last 10 when filtering by date range
        } else {
            stream = stream.sorted((a,b) -> b.getTimestamp().compareTo(a.getTimestamp())).limit(10);
        }
        return stream.map(h -> {
            HistoryDTO dto = new HistoryDTO();
            dto.id = h.getId();
            dto.vehicleId = h.getVehicle() != null ? h.getVehicle().getId() : null;
            dto.description = h.getDescription();
            dto.timestamp = h.getTimestamp();
            // attempt to surface related work order status if available
            try {
                if (h.getWorkOrder() != null) {
                    dto.status = String.valueOf(h.getWorkOrder().getStatus());
                }
            } catch (Exception ignored) {
            }
            return dto;
        }).collect(Collectors.toList());
    }
}
