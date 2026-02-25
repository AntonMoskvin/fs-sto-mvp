package com.sfsto;

import com.sfsto.model.Station;
import com.sfsto.model.Service;
import com.sfsto.model.WorkOption;
import com.sfsto.model.StationService;
import com.sfsto.repository.StationRepository;
import com.sfsto.repository.ServiceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class SeedData {
    @Bean
    public CommandLineRunner seed(StationRepository stationRepo, ServiceRepository serviceRepo, com.sfsto.repository.WorkOptionRepository workOptionRepo) {
        return args -> {
            if (stationRepo.count() == 0) {
                Station s = new Station();
                s.setName("Demo СТО");
                s.setAddress("Москва, ул. Примерная 1");
                s.setLatitude(55.75);
                s.setLongitude(37.62);
                s.setTimezone("Europe/Moscow");
                stationRepo.save(s);
            }
            if (serviceRepo.count() == 0) {
                Service srv1 = new Service();
                srv1.setName("Компьютерная диагностика");
                srv1.setDurationMinutes(45);
                srv1.setPrice(1500);
                serviceRepo.save(srv1);
            }
            if (workOptionRepo.count() == 0) {
                WorkOption w1 = new WorkOption();
                w1.setName("ТО");
                w1.setDescription("Техническое обслуживание");
                w1.setDurationMinutes(60);
                workOptionRepo.save(w1);
                WorkOption w2 = new WorkOption();
                w2.setName("Замена масла");
                w2.setDescription("Замена моторного масла");
                w2.setDurationMinutes(30);
                workOptionRepo.save(w2);
                WorkOption w3 = new WorkOption();
                w3.setName("Диагностика");
                w3.setDescription("Диагностика систем");
                w3.setDurationMinutes(45);
                workOptionRepo.save(w3);
            }
        };
    }
}
