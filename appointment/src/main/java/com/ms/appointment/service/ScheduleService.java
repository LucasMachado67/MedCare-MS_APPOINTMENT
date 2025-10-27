package com.ms.appointment.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ms.appointment.models.Schedule;
import com.ms.appointment.repository.ScheduleRepository;

@Service
public class ScheduleService{

    @Autowired
    private ScheduleRepository repository;
    @Autowired
    private EntityManegementSystem entityManegementSystem;
    
    public Schedule save(Schedule schedule){
        Boolean medicExists = entityManegementSystem.medicExists(schedule.getMedicId());

        if(medicExists == false){
            throw new IllegalArgumentException("Invalid medic ID");
        }
        
        return repository.save(schedule);
    }

    public boolean isWithinSchedule(long medicId, LocalDateTime dateTime) {
        DayOfWeek day = dateTime.getDayOfWeek();
        //Validacao se medico existe no service de entidades
        Boolean medicExists = entityManegementSystem.medicExists(medicId);

        if(medicExists == false){
            return false;
        }

        Schedule schedule = repository.findByMedicIdAndDayOfWeek(medicId, day);

        if (schedule == null) return false;

        return !dateTime.toLocalTime().isBefore(schedule.getStartTime()) &&
               !dateTime.toLocalTime().isAfter(schedule.getEndTime());
    }
}
