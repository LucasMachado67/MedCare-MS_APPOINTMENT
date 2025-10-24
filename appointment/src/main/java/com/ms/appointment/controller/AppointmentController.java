package com.ms.appointment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ms.appointment.dtos.AppointmentRequestDTO;
import com.ms.appointment.models.Appointment;
import com.ms.appointment.service.AppointmentService;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {


    @Autowired
    private AppointmentService service;

    @PostMapping()
    public ResponseEntity<Appointment> create(@RequestBody AppointmentRequestDTO dto){
        Appointment created = service.createAppointment(dto);
        return ResponseEntity.ok(created);
    }
}
