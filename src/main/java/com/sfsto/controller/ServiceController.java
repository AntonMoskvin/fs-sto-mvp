package com.sfsto.controller;

import com.sfsto.model.Service;
import com.sfsto.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ServiceController {
    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping("/services")
    public List<Service> list() {
        return serviceRepository.findAll();
    }
}
