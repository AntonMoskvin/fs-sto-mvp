package com.sfsto;

import com.sfsto.model.Station;
import com.sfsto.model.Service;
import com.sfsto.repository.StationRepository;
import com.sfsto.repository.ServiceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class SeedData {
    @Bean
    public CommandLineRunner seed(StationRepository stationRepo, ServiceRepository serviceRepo) {
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
        };
    }
}
