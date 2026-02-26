package com.sfsto.controller;

import com.sfsto.dto.HistoryDTO;
import com.sfsto.model.HistoryEntry;
import com.sfsto.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/history/export")
public class HistoryExportController {
    @Autowired
    private HistoryRepository historyRepository;

    @GetMapping
    public ResponseEntity<byte[]> exportCsv(@RequestParam(required = false) String from,
                                            @RequestParam(required = false) String to) {
        List<HistoryDTO> items = historyRepository.findAll().stream().map(h -> {
            HistoryDTO dto = new HistoryDTO();
            dto.id = h.getId();
            dto.vehicleId = h.getVehicle() != null ? h.getVehicle().getId() : null;
            dto.description = h.getDescription();
            dto.timestamp = h.getTimestamp();
            return dto;
        }).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        sb.append("timestamp,vehicleId,description\n");
        for (HistoryDTO it : items) {
            sb.append(it.timestamp).append(',')
              .append(it.vehicleId == null ? "" : it.vehicleId).append(',')
              .append(it.description == null ? "" : it.description.replace(',', ' '))
              .append('\n');
        }
        byte[] data = sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=history.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(data.length)
                .body(data);
    }
}
