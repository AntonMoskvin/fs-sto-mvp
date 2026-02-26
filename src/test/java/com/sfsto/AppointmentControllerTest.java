package com.sfsto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@Transactional
//public class AppointmentControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    private Long ensureStationId() throws Exception {
//        // Try real stations first
//        String resp = mockMvc.perform(get("/api/v1/stations"))
//                .andReturn().getResponse().getContentAsString();
//        List<Map<String, Object>> stations = mapper.readValue(resp, new TypeReference<List<Map<String, Object>>>(){});
//        if (stations != null && !stations.isEmpty()) {
//            Object id = stations.get(0).get("id");
//            if (id instanceof Number) return ((Number) id).longValue();
//        }
//        // Fallback to demo endpoint
//        String demo = mockMvc.perform(get("/api/v1/stations/demo"))
//                .andReturn().getResponse().getContentAsString();
//        List<Map<String, Object>> d = mapper.readValue(demo, new TypeReference<List<Map<String, Object>>>(){});
//        if (d != null && !d.isEmpty()) {
//            Object id = d.get(0).get("id");
//            if (id instanceof Number) return ((Number) id).longValue();
//        }
//        throw new RuntimeException("No station id available for tests");
//    }
//
//    private Long firstWorkOptionId() throws Exception {
//        String json = mockMvc.perform(get("/api/v1/workoptions")).andReturn().getResponse().getContentAsString();
//        List<Map<String, Object>> opts = mapper.readValue(json, new TypeReference<List<Map<String, Object>>>(){});
//        if (opts != null && !opts.isEmpty()) {
//            Object id = opts.get(0).get("id");
//            if (id instanceof Number) return ((Number) id).longValue();
//        }
//        return null;
//    }
//
//    @Test
//    public void bookingFlow_CreationAndOverlap() throws Exception {
//        Long stationId = ensureStationId();
//        Long workId = firstWorkOptionId();
//        // 1) Create initial appointment
//        Map<String, Object> payload1 = new HashMap<>();
//        payload1.put("stationId", stationId);
//        payload1.put("vehicleId", null);
//        payload1.put("startTime", "2026-03-01T10:00");
//        payload1.put("workOptionIds", workId != null ? List.of(workId) : null);
//
//        MvcResult r1 = mockMvc.perform(post("/api/v1/appointments")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(payload1)))
//                .andReturn();
//        int status1 = r1.getResponse().getStatus();
//        if (status1 != 200) {
//            // fallback: accept 400 if lack of data; but print for debugging
//            throw new AssertionError("Expected 200, got " + status1);
//        }
//        // 2) Overlapping booking at the same time
//        Map<String, Object> payload2 = new HashMap<>();
//        payload2.put("stationId", stationId);
//        payload2.put("vehicleId", null);
//        payload2.put("startTime", "2026-03-01T10:00");
//        payload2.put("workOptionIds", workId != null ? List.of(workId) : null);
//        MvcResult r2 = mockMvc.perform(post("/api/v1/appointments")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(payload2)))
//                .andReturn();
//        int status2 = r2.getResponse().getStatus();
//        // Accept either 409 or 400 as an assertion depending on validation timing
//        if (status2 != 409 && status2 != 400) {
//            throw new AssertionError("Expected 409 or 400, got " + status2);
//        }
//    }
//
//    @Test
//    public void loadWorkOptionsAvailable() throws Exception {
//        mockMvc.perform(get("/api/v1/workoptions")).andExpect(status().isOk());
//    }
//}
