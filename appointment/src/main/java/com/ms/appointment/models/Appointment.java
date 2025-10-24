package com.ms.appointment.models;

import java.time.LocalDateTime;

import com.ms.appointment.enums.AppointmentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long medic_id;
    private long patient_id;
    private LocalDateTime date;
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
    private int duration;
    private String room;
    private String observation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = AppointmentStatus.SCHEDULED;
        this.duration = 30; // default
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Appointment(){}

    public Appointment(long medic_id, long patient_id, LocalDateTime date, int duration, String room, String observation) {
        setMedicId(medic_id);
        setPatientId(patient_id);
        setDate(date);
        this.status = AppointmentStatus.SCHEDULED;
        setDuration(duration);
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
        return medic_id;
    }
    public void setMedicId(long medic_id) {
        this.medic_id = medic_id;
    }
    public long getPatientId() {
        return patient_id;
    }
    public void setPatientId(long patient_id) {
        this.patient_id = patient_id;
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
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
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

    public long getMedic_id() {
        return medic_id;
    }

    public void setMedic_id(long medic_id) {
        this.medic_id = medic_id;
    }

    public long getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(long patient_id) {
        this.patient_id = patient_id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    

}
