package com.ms.appointment.controller;

import java.net.URI;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ms.appointment.dtos.AppointmentRequestDTO;
import com.ms.appointment.models.Appointment;
import com.ms.appointment.service.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("appointment")
@Tag(name = "Appointments", description = "Endpoints to manage Appointments")
public class AppointmentController {


    @Autowired
    private AppointmentService service;

    @Operation(summary = "Create an appointment", description = "Create a new appointment and save in the database")
    @PostMapping("/create")
    public ResponseEntity<Appointment> create(@Valid @RequestBody AppointmentRequestDTO dto) throws Exception{
        Appointment created = service.createAppointment(dto);

        URI location = URI.create("/appointment/" + created.getId());

        return ResponseEntity.created(location).body(created);
    }
    @Operation(summary = "Find all appointments by Patient Id", description = "Return all appointments that the patient has done or is in schedule")
    @GetMapping("/patient/{id}")
    public ResponseEntity<List<Appointment>> findByPatientId(@PathVariable long id){
        List<Appointment> appointments = service.findByPatient(id);
        return ResponseEntity.ok(appointments);
    }
    
    @Operation(summary = "Find all appointments by Medic Id", description = "Return all appointments that the medic has done or is in schedule")
    @GetMapping("/medic/{id}")
    public ResponseEntity<List<Appointment>> findByMedic(@PathVariable long id){
        List<Appointment> appointments = service.findByMedic(id);
        return ResponseEntity.ok(appointments);
    }
    @Operation(summary = "Update Appointment Information", description = "Save in the database new informations about an appointment")
    @PutMapping("/{id}")
    public ResponseEntity<Appointment> update(@PathVariable long id, @Valid @RequestBody AppointmentRequestDTO dto) throws JsonProcessingException {
        
        Appointment updated = service.updateAppointment(id, dto);
        return ResponseEntity.ok(updated);
    }
    @Operation(summary = "Update appointment status", description = "Update the appointment stauts to CANCELLED")
    @PutMapping("/cancel/{id}")
    public ResponseEntity<Appointment> cancel(@PathVariable long id){
        var appointmentCancelled = service.cancelAppointment(id);
        return ResponseEntity.ok(appointmentCancelled);
    }
    @Operation(summary = "Update appointment status", description = "Update the appointment stauts to COMPLETED")
    @PutMapping("/complete/{id}")
    public ResponseEntity<Appointment> complete(@PathVariable long id){
        var completed = service.completeAppointment(id);
        return ResponseEntity.ok(completed);
    }
}
