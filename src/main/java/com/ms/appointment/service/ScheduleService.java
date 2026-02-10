package com.ms.appointment.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ms.appointment.dtos.PersonDto;
import com.ms.appointment.dtos.ScheduleCreationDTO;
import com.ms.appointment.models.Schedule;
import com.ms.appointment.producer.ScheduleProducer;
import com.ms.appointment.repository.ScheduleRepository;

@Service
public class ScheduleService{

    @Autowired
    private ScheduleRepository repository;
    @Autowired
    private EntityManagementSystem entityManagementSystem;
    @Autowired
    private ScheduleProducer scheduleProducer;

    //Cria uma Schedule para o medic selecionado
    public Schedule save(ScheduleCreationDTO dto) throws JsonProcessingException {
        boolean medicExists = entityManagementSystem.medicExists(dto.getMedicId());

        if(!medicExists){
            throw new IllegalArgumentException("Invalid medic ID");
        }

        Schedule schedule = new Schedule();
        schedule.setMedicId(dto.getMedicId());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setDayOfWeek(dto.getDayOfWeek());
        //Pegando as informações do médico
        PersonDto medicDto = entityManagementSystem.findPersonByIdToSendEmail(schedule.getMedicId());
        //Passando as informações para ser enviado e-mail para o médico
        scheduleProducer.publishScheduleCreated(medicDto, schedule);

        return repository.save(schedule);
    }
    //Verifica se o horário está dentro do horário de trabalho do médico
    public boolean isWithinSchedule(long medicId, LocalDateTime startTime, LocalDateTime endTime) {
        DayOfWeek day = startTime.getDayOfWeek();
        //Validacao se medico existe no service de entidades
        boolean medicExists = entityManagementSystem.medicExists(medicId);

        if(!medicExists){
            return false;
        }

        Schedule schedule = repository.findByMedicIdAndDayOfWeek(medicId, day);

        if (schedule == null) return false;

        return !startTime.toLocalTime().isBefore(schedule.getStartTime()) &&
               !endTime.toLocalTime().isAfter(schedule.getEndTime());
    }
    //Carrega todas as schedules por médico
    public List<Schedule> findByMedicId(long medicId){

        List<Schedule> lista = repository.findByMedicId(medicId);

        return lista;
    }
}
