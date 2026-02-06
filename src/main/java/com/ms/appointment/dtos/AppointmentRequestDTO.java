package com.ms.appointment.dtos;

import java.time.LocalDateTime;

public class AppointmentRequestDTO {
    
    private long patientId;
    private long medicId;
    private LocalDateTime starTime;
    private LocalDateTime endTime;
    private String observation;
    private String room;


    
    public LocalDateTime getStarTime() {
        return starTime;
    }
    public void setStarTime(LocalDateTime starTime) {
        this.starTime = starTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    public long getPatientId() {
        return patientId;
    }
    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }
    public long getMedicId() {
        return medicId;
    }
    public void setMedicId(long medicId) {
        this.medicId = medicId;
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
