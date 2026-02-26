package com.sfsto.controller;

import com.sfsto.dto.HistoryDTO;
import com.sfsto.model.HistoryEntry;
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
    public List<HistoryDTO> list() {
        return historyRepository.findAll().stream().map(h -> {
            HistoryDTO dto = new HistoryDTO();
            dto.id = h.getId();
            dto.vehicleId = h.getVehicle() != null ? h.getVehicle().getId() : null;
            dto.description = h.getDescription();
            dto.timestamp = h.getTimestamp();
            return dto;
        }).collect(Collectors.toList());
    }
}
