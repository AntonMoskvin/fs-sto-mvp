package com.sfsto.controller;

import com.sfsto.model.Station;
import com.sfsto.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/stations")
public class StationController {
    @Autowired
    private StationRepository stationRepository;

    @GetMapping(produces = "application/json")
    public List<Station> list() {
        // For MVP, return all stations in DB; if DB unavailable, return sample data for demo
        try {
            List<Station> all = stationRepository.findAll();
            if (all != null && !all.isEmpty()) return all;
        } catch (Exception ignored) {
        }
        Station s1 = new Station();
        s1.setId(1L);
        s1.setName("Demo СТО Москва");
        s1.setAddress("Москва, Красная площадь");
        s1.setLatitude(55.7558);
        s1.setLongitude(37.6173);
        s1.setTimezone("Europe/Moscow");
        Station s2 = new Station();
        s2.setId(2L);
        s2.setName("Demo СТО Москва 2");
        s2.setAddress("Москва, Тверская 12");
        s2.setLatitude(55.7519);
        s2.setLongitude(37.5813);
        s2.setTimezone("Europe/Moscow");
        return Arrays.asList(s1, s2);
    }

    @GetMapping("/{id}")
    public Station get(@PathVariable Long id) {
        return stationRepository.findById(id).orElse(null);
    }

    @GetMapping(value = "/demo", produces = "application/json")
    public List<Station> demoList() {
        Station s1 = new Station();
        s1.setId(101L);
        s1.setName("Demo СТО Москва (demo)");
        s1.setAddress("Москва, Demo Street 1");
        s1.setLatitude(55.76);
        s1.setLongitude(37.64);
        s1.setTimezone("Europe/Moscow");
        return java.util.Arrays.asList(s1);
    }
}
