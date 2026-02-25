package com.sfsto.controller;

import com.sfsto.model.WorkOption;
import com.sfsto.repository.WorkOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class WorkOptionController {
    @Autowired
    private WorkOptionRepository workOptionRepository;

    @GetMapping("/workoptions")
    public List<WorkOption> list() {
        return workOptionRepository.findAll();
    }
}
