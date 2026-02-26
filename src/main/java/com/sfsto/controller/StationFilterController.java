package com.sfsto.controller;

import com.sfsto.model.Station;
import com.sfsto.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class StationFilterController {
    @Autowired
    private StationRepository stationRepository;

    @GetMapping(value = "/stations", params = {"serviceId"})
    public List<Station> byService(@RequestParam Long serviceId) {
        return stationRepository.findByServiceId(serviceId);
    }
}
