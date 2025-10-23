package com.ms.appointment.models;

import java.time.Duration;
import java.time.LocalDateTime;

import com.ms.appointment.enums.AppointmentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long medicId;
    private long patientId;
    private LocalDateTime date;
    private AppointmentStatus status;
    private String cancellationReason;
    private Duration duration;
    private String room;
    private String observation;

    public Appointment(){}

    public Appointment(long medicId, long patientId, LocalDateTime date, Duration duration,String cancellationReason, String room, String observation) {
        setMedicId(medicId);
        setPatientId(patientId);
        setDate(date);
        this.status = AppointmentStatus.SCHEDULED;
        setDuration(duration);
        setCancellationReason(cancellationReason);
        setRoom(room);
        setObservation(observation);
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getMedicId() {
        return medicId;
    }
    public void setMedicId(long medicId) {
        this.medicId = medicId;
    }
    public long getPatientId() {
        return patientId;
    }
    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }
    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public AppointmentStatus getStatus() {
        return status;
    }
    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
    public String getCancellationReason() {
        return cancellationReason;
    }
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
    public Duration getDuration() {
        return duration;
    }
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getRoom(){
        return this.room;
    }

    public void setRoom(String room){
        this.room = room;
    }

    public String getObservation(){
        return this.observation;
    }

    public void setObservation(String observation){
        this.observation = observation;
    }

}
