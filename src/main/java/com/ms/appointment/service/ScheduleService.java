package com.ms.appointment.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ms.appointment.dtos.PersonDto;
import com.ms.appointment.models.Schedule;
import com.ms.appointment.producer.ScheduleProducer;
import com.ms.appointment.repository.ScheduleRepository;

@Service
public class ScheduleService{

    @Autowired
    private ScheduleRepository repository;
    @Autowired
    private EntityManagementSystem entityManegementSystem;
    @Autowired
    private ScheduleProducer scheduleProducer;

    //Cria uma Schedule para o medic selecionado
    public Schedule save(Schedule schedule) throws JsonProcessingException {
        boolean medicExists = entityManegementSystem.medicExists(schedule.getMedicId());

        if(!medicExists){
            throw new IllegalArgumentException("Invalid medic ID");
        }
        //Pegando as informações do médico
        PersonDto medicDto = entityManegementSystem.findPersonByIdToSendEmail(schedule.getMedicId());
        //Passando as informações para ser enviado e-mail para o médico
        scheduleProducer.publishScheduleCreated(medicDto, schedule);

        return repository.save(schedule);
    }
    //Verifica se o horário já está preenchido
    public boolean isWithinSchedule(long medicId, LocalDateTime dateTime) {
        DayOfWeek day = dateTime.getDayOfWeek();
        //Validacao se medico existe no service de entidades
        boolean medicExists = entityManegementSystem.medicExists(medicId);

        if(!medicExists){
            return false;
        }

        Schedule schedule = repository.findByMedicIdAndDayOfWeek(medicId, day);

        if (schedule == null) return false;

        return !dateTime.toLocalTime().isBefore(schedule.getStartTime()) &&
               !dateTime.toLocalTime().isAfter(schedule.getEndTime());
    }
}
