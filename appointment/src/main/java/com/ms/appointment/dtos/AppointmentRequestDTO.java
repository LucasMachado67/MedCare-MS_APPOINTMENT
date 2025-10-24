package com.ms.appointment.dtos;

import java.time.LocalDateTime;

public class AppointmentRequestDTO {

    private long patient_id;
    private long medic_id;
    private LocalDateTime dateTime;
    private String observation;
    private String room;

    
    public long getPatient_id() {
        return patient_id;
    }
    public void setPatient_id(long patient_id) {
        this.patient_id = patient_id;
    }
    public long getMedic_id() {
        return medic_id;
    }
    public void setMedic_id(long medic_id) {
        this.medic_id = medic_id;
    }
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    public String getObservation() {
        return observation;
    }
    public void setObservation(String observation) {
        this.observation = observation;
    }
    public String getRoom() {
        return room;
    }
    public void setRoom(String room) {
        this.room = room;
    }

    
}
