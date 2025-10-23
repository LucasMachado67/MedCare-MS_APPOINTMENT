package com.ms.appointment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ms.appointment.dtos.ScheduleCreationDTO;
import com.ms.appointment.models.Schedule;
import com.ms.appointment.service.ScheduleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService service;

    @PostMapping("/availability")
    public ResponseEntity<Schedule> createSchedule(@RequestBody @Valid ScheduleCreationDTO dto){
        try{
            Schedule newSchedule = service.createSchedule(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(newSchedule);
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }
}
