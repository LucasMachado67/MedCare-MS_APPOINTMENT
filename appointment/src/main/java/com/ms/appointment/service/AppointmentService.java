package com.ms.appointment.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ms.appointment.dtos.AppointmentRequestDTO;
import com.ms.appointment.enums.AppointmentStatus;
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
        if (!entityClient.patientExists(dto.getPatientId()))
            throw new IllegalArgumentException("Patient not found");

        if (!entityClient.medicExists(dto.getMedicId()))
            throw new IllegalArgumentException("Medic not found");

        // Verifica se o medico possui agenda configurada neste horário
        if (!scheduleService.isWithinSchedule(dto.getMedicId(), dto.getDateTime()))
            throw new IllegalArgumentException("Médico não possui horário configurado para essa data/hora");

        // 3️- Verifica se já existe outro agendamento nesse horário
        LocalDateTime start = dto.getDateTime();
        LocalDateTime end = start.plusMinutes(30);
        List<Appointment> conflicts = repository.findConflicts(dto.getMedicId(), start, end);

        if (!conflicts.isEmpty())
            throw new IllegalArgumentException("Horário já ocupado");

        // 4️- Cria a consulta
        Appointment appointment = new Appointment();
        appointment.setPatientId(dto.getPatientId());
        appointment.setMedicId(dto.getMedicId());
        appointment.setDate(dto.getDateTime());
        appointment.setObservation(dto.getObservation());
        appointment.setRoom(dto.getRoom());

        return repository.save(appointment);
    }

    public List<Appointment> findByPatient(long id){
        List<Appointment> foundAppointments = repository.findByPatientId(id);

        if(foundAppointments.size() <= 0){
            throw new RuntimeException("No Appointment found this patient");
        }else{
            return foundAppointments;
        }

    }

    public List<Appointment> findByMedic(long id){
        List<Appointment> foundAppointments = repository.findByMedicId(id);

        if(foundAppointments.size() <= 0){
            throw new RuntimeException("No Appointment found this Medic");
        }else{
            return foundAppointments;
        }

    }

    public Appointment updateAppointment(long id, AppointmentRequestDTO dto){

        Appointment existing = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        
        if(existing.getStatus() == AppointmentStatus.CANCELED)
            throw new IllegalArgumentException("Appointment already canceled, can not be updated");

        if (!entityClient.patientExists(dto.getPatientId()))
            throw new IllegalArgumentException("Paciente não encontrado ou inativo");

        if (!entityClient.medicExists(dto.getMedicId()))
            throw new IllegalArgumentException("Médico não encontrado ou inativo");

        if (!scheduleService.isWithinSchedule(dto.getMedicId(), dto.getDateTime()))
            throw new IllegalArgumentException("Médico não possui horário configurado para essa data/hora");

        LocalDateTime start = dto.getDateTime();
        LocalDateTime end  = start.plusMinutes(30);
        List<Appointment> conflicts = repository.findConflicts(dto.getMedicId(), start, end);
        boolean hasConflict = conflicts.stream().anyMatch(a -> a.getId() != id);

        if(hasConflict)
            throw new IllegalArgumentException("Schedule already taken");

        existing.setMedicId(dto.getMedicId());
        existing.setPatientId(dto.getPatientId());
        existing.setDate(dto.getDateTime());
        existing.setObservation(dto.getObservation());
        existing.setStatus(AppointmentStatus.SCHEDULED);
        existing.setRoom(dto.getRoom());

        return repository.save(existing);
    }

    @Transactional
    public void cancelAppointment(long id){
        Appointment existing = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        if(existing.getStatus() == AppointmentStatus.CANCELED)
            throw new IllegalArgumentException("Appointment already canceled");

        existing.setStatus((AppointmentStatus.CANCELED));
        repository.save(existing);
    }

    @Transactional
    public Appointment completeAppointment(long id){
        Appointment existing = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        existing.setStatus(AppointmentStatus.COMPLETED);
        return repository.save(existing);
    }



}
