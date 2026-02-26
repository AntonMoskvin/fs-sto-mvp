package com.sfsto.controller;

import com.sfsto.model.HistoryEntry;
import com.sfsto.model.WorkOrder;
import com.sfsto.repository.HistoryRepository;
import com.sfsto.repository.WorkOrderRepository;
import com.sfsto.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the History API endpoint.
 */
@SpringBootTest(properties = {
  // Disable Flyway migrations in tests to avoid DB incompatibilities
  "spring.flyway.enabled=false",
  // Let Hibernate create-drop schema from entities for clean tests
  "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.ANY)
@org.springframework.transaction.annotation.Transactional
public class HistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @BeforeEach
    void cleanAll() {
        historyRepository.deleteAll();
        workOrderRepository.deleteAll();
        vehicleRepository.deleteAll();
    }

    @Test
    void listReturnsLastTenWhenNoFilter() throws Exception {
        LocalDateTime base = LocalDateTime.now().withNano(0);
        for (int i = 0; i < 15; i++) {
            HistoryEntry h = new HistoryEntry();
            h.setDescription("desc_" + i);
            h.setTimestamp(base.minusMinutes(i));
            historyRepository.save(h);
        }

        mockMvc.perform(get("/api/v1/history").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)));
    }

    @Test
    void listFiltersByFromTo() throws Exception {
        LocalDateTime t1 = LocalDateTime.of(2026, 2, 26, 10, 0, 0);

        HistoryEntry h1 = new HistoryEntry();
        h1.setDescription("h1");
        h1.setTimestamp(t1);
        historyRepository.save(h1);

        HistoryEntry h2 = new HistoryEntry();
        h2.setDescription("h2");
        h2.setTimestamp(t1.plusHours(2)); // 12:00
        historyRepository.save(h2);

        HistoryEntry h3 = new HistoryEntry();
        h3.setDescription("h3");
        h3.setTimestamp(t1.plusHours(25)); // next day 11:00
        historyRepository.save(h3);

        // From 2026-02-26T11:00:00 to 2026-02-27T00:00:00 should include only h2
        mockMvc.perform(
                        get("/api/v1/history?from=2026-02-26T11:00:00&to=2026-02-27T00:00:00")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].timestamp").value("2026-02-26T12:00:00"));
    }

    @Test
    void historyIncludesStatusFromWorkOrder() throws Exception {
        // Ensure clean slate
        historyRepository.deleteAll();
        workOrderRepository.deleteAll();

        WorkOrder wo = new WorkOrder();
        wo.setStatus("COMPLETED");
        wo.setDescription("WO");
        workOrderRepository.save(wo);

        HistoryEntry h = new HistoryEntry();
        h.setDescription("with status");
        h.setTimestamp(LocalDateTime.now().withNano(0));
        h.setWorkOrder(wo);
        historyRepository.save(h);

        mockMvc.perform(
                        get("/api/v1/history?from=1970-01-01T00:00:00&to=2100-01-01T00:00:00")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
    }
}
