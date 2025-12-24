package com.ms.appointment.dtos;

import java.time.LocalDateTime;

public class AppointmentRequestDTO {
    
    private long patientId;
    private long medicId;
    private LocalDateTime dateTime;
    private String observation;
    private String room;

    
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
