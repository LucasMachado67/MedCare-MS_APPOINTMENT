package com.ms.appointment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ms.appointment.models.Schedule;
import com.ms.appointment.service.ScheduleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequestMapping("schedule")
public class ScheduleController {


    @Autowired
    private ScheduleService service;

    @PostMapping("/create")
    public ResponseEntity<Schedule> create(@RequestBody Schedule schedule) throws JsonProcessingException {
        return ResponseEntity.ok(service.save(schedule));
    }
    
}
