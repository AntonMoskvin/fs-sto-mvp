package com.sfsto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    private Long fetchFirstWorkOptionId() throws Exception {
        String json = mockMvc.perform(get("/api/v1/workoptions"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        if (json == null || json.isEmpty()) return null;
        List<Map<String, Object>> items = mapper.readValue(json, new TypeReference<List<Map<String, Object>>>(){});
        if (items == null || items.isEmpty()) return null;
        Object id = items.get(0).get("id");
        if (id instanceof Number) return ((Number) id).longValue();
        return null;
    }

    @Test
    public void createAppointmentWorks() throws Exception {
        Long workId = fetchFirstWorkOptionId();
        if (workId == null) workId = 1L;
        Map<String, Object> payload = new HashMap<>();
        payload.put("stationId", 1);
        payload.put("vehicleId", null);
        payload.put("startTime", "2026-03-01T10:00");
        payload.put("workOptionIds", List.of(workId));

        mockMvc.perform(post("/api/v1/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.startTime").exists());
    }

    @Test
    public void bookingOverlapShouldConflict() throws Exception {
        Long workId = fetchFirstWorkOptionId();
        if (workId == null) workId = 1L;
        Map<String, Object> payload = new HashMap<>();
        payload.put("stationId", 1);
        payload.put("vehicleId", null);
        payload.put("startTime", "2026-03-01T11:00");
        payload.put("workOptionIds", List.of(workId));

        // First booking
        mockMvc.perform(post("/api/v1/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andReturn();

        // Second booking at overlapping time
        payload.put("startTime", "2026-03-01T11:15");
        mockMvc.perform(post("/api/v1/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isConflict());
    }

    @Test
    public void workOptionsAreAvailable() throws Exception {
        mockMvc.perform(get("/api/v1/workoptions"))
                .andExpect(status().isOk());
    }
}
