package com.ms.appointment.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ms.appointment.dtos.AppointmentRequestDTO;
import com.ms.appointment.models.Appointment;
import com.ms.appointment.repository.AppointmentRepository;


@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository repository;
    @Autowired
    private EntityManegementSystem entityClient;
    @Autowired
    private ScheduleService scheduleService;

    @Transactional
    public Appointment createAppointment(AppointmentRequestDTO dto){
        
        // 1- Verifica se paciente e médico existem e estão ativos
        if (!entityClient.isPatientActive(dto.getPatient_id()))
            throw new IllegalArgumentException("Patient not found");

        if (!entityClient.isMedicActive(dto.getMedic_id()))
            throw new IllegalArgumentException("Medic not found");

        // Verifica se o medico possui agenda configurada neste horário
        if (!scheduleService.isWithinSchedule(dto.getMedic_id(), dto.getDateTime()))
            throw new IllegalArgumentException("Médico não possui horário configurado para essa data/hora");

        // 3️- Verifica se já existe outro agendamento nesse horário
        LocalDateTime start = dto.getDateTime();
        LocalDateTime end = start.plusMinutes(30);
        List<Appointment> conflicts = repository.findConflicts(dto.getMedic_id(), start, end);

        if (!conflicts.isEmpty())
            throw new IllegalArgumentException("Horário já ocupado");

        // 4️- Cria a consulta
        Appointment appointment = new Appointment();
        appointment.setPatientId(dto.getPatient_id());
        appointment.setMedicId(dto.getMedic_id());
        appointment.setDate(dto.getDateTime());
        appointment.setObservation(dto.getObservation());
        appointment.setRoom(dto.getRoom());

        return repository.save(appointment);
    }

}
