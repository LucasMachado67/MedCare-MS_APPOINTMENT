package com.ms.appointment.models;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name= "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long medicId;
    private LocalTime startTime;
    private LocalTime endTime;
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    public Schedule(){}

    public Schedule(long medicId, LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek) {
        setMedicId(medicId);
        setDayOfWeek(dayOfWeek);
        setStartTime(startTime);
        setEndTime(endTime);
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

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public DayOfWeek getDayOfWeek(){
        return this.dayOfWeek;
    }
    public void setDayOfWeek(DayOfWeek day){
        if(day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY)
            throw new IllegalArgumentException("Invalid Day");
        this.dayOfWeek = day;
    }

    
}
