package com.ms.appointment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ms.appointment.dtos.AppointmentRequestDTO;
import com.ms.appointment.models.Appointment;
import com.ms.appointment.service.AppointmentService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/appointments")
public class AppointmentController {


    @Autowired
    private AppointmentService service;

    @PostMapping()
    public ResponseEntity<Appointment> create(@RequestBody AppointmentRequestDTO dto) throws Exception{
        Appointment created = service.createAppointment(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<List<Appointment>> findByPatientId(@PathVariable long id){
        List<Appointment> appointments = service.findByPatient(id);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/medic/{id}")
    public ResponseEntity<List<Appointment>> findByMedic(@PathVariable long id){
        List<Appointment> appointments = service.findByMedic(id);
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Appointment> update(@PathVariable long id, @RequestBody AppointmentRequestDTO dto) {
        
        Appointment updated = service.updateAppointment(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable long id){
        service.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Appointment> complete(@PathVariable long id){
        Appointment completed = service.completeAppointment(id);
        return ResponseEntity.ok(completed);
    }
}
