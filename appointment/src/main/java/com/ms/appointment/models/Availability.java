package com.ms.appointment.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

//Entidade que indica horários livres de médicos
public class Availability {

    private long id;
    private long medic_id;
    private LocalDate date;
    private List<LocalTime> availableSlots;

    public Availability(long medic_id, LocalDate date, LocalTime availableSlot){
        setMedic_id(medic_id);
        setDate(date);
        setAvailableSlots(availableSlots);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMedic_id() {
        return medic_id;
    }

    public void setMedic_id(long medic_id) {
        this.medic_id = medic_id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<LocalTime> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(List<LocalTime> availableSlots) {
        this.availableSlots = availableSlots;
    }

    

}
