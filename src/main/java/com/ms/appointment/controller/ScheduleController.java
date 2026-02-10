package com.ms.appointment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ms.appointment.dtos.ScheduleCreationDTO;
import com.ms.appointment.models.Schedule;
import com.ms.appointment.service.ScheduleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("schedule")
@Tag(name = "Schedules", description = "Endpoints to manage Schedules of medics")
public class ScheduleController {

    @Autowired
    private ScheduleService service;

    @Operation(summary = "Create an schedule", description = "Create a new schedule and save in the database")
    @PostMapping("/create")
    public ResponseEntity<Schedule> create(@Valid @RequestBody ScheduleCreationDTO schedule) throws JsonProcessingException {
        Schedule created = service.save(schedule);

        URI location = URI.create("/schedule/" + created.getId());

        return ResponseEntity.created(location).body(created);
    }
    @Operation(summary = "List schedules by medicId", description = "Lists all schedules that a medic has configured")
    @GetMapping("/{medicId}")
    public ResponseEntity<List<Schedule>> findById(@PathVariable long medicId){

        return ResponseEntity.ok(
            service.findByMedicId(medicId)
        );
    }
    
}
