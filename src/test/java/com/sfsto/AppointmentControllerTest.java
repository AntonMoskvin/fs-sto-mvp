package com.sfsto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
public class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void createAppointmentWorks() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("stationId", 1);
        payload.put("vehicleId", (Object) null);
        payload.put("startTime", "2026-03-01T10:00");
        payload.put("workOptionIds", java.util.Arrays.asList(1L));

        mockMvc.perform(post("/api/v1/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.startTime").exists());
    }

    @Test
    public void overlapBookingShouldConflict() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("stationId", 1);
        payload.put("vehicleId", (Object) null);
        payload.put("startTime", "2026-03-01T11:00");
        payload.put("workOptionIds", java.util.Arrays.asList(1L));

        // Create first appointment
        mockMvc.perform(post("/api/v1/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        // Try to create overlapping appointment at 11:15
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
