package com.sfsto.model;

import jakarta.persistence.*;

@Entity
@Table(name = "station_services")
public class StationService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Station getStation() { return station; }
    public void setStation(Station station) { this.station = station; }
    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }
}
