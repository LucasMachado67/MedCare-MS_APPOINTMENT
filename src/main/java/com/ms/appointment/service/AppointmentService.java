package com.ms.appointment.service;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ms.appointment.dtos.AppointmentRequestDTO;
import com.ms.appointment.dtos.PersonDto;
import com.ms.appointment.enums.AppointmentStatus;
import com.ms.appointment.models.Appointment;
import com.ms.appointment.producer.AppointmentProducer;
import com.ms.appointment.repository.AppointmentRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository repository;
    @Autowired
    private EntityManagementSystem entityClient;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private AppointmentProducer appointmentProducer;

    @Transactional
    public Appointment createAppointment(AppointmentRequestDTO dto) throws Exception{
        
        // 1- Verifica se paciente e médico existem e estão ativos
        if (!entityClient.medicExists(dto.getMedicId()))
            throw new IllegalArgumentException("Medic not found");

        if (!entityClient.patientExists(dto.getPatientId()))
            throw new IllegalArgumentException("Patient not found");

        // Verifica se o médico possui agenda configurada neste horário
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
        appointment.setDateTime(dto.getDateTime());
        appointment.setObservation(dto.getObservation());
        appointment.setRoom(dto.getRoom());

        // 5. Notificacao via e-mail para patient e medic
        PersonDto patientDto = entityClient.findPersonByIdToSendEmail(dto.getPatientId());
        PersonDto medicDto = entityClient.findPersonByIdToSendEmail(dto.getMedicId());

        appointmentProducer.publishAppointmentCreated(patientDto, appointment);
        appointmentProducer.publishAppointmentCreated(medicDto, appointment);

        return repository.save(appointment);

    }

    public List<Appointment> findByPatient(long id){
        List<Appointment> foundAppointments = repository.findByPatientId(id);

        if(foundAppointments.isEmpty()){
            throw new RuntimeException("No Appointment found this patient");
        }else{
            return foundAppointments;
        }

    }

    public List<Appointment> findByMedic(long id){
        List<Appointment> foundAppointments = repository.findByMedicId(id);

        if(foundAppointments.isEmpty()){
            throw new RuntimeException("No Appointment found this Medic");
        }else{
            return foundAppointments;
        }

    }

    public Appointment updateAppointment(long id, AppointmentRequestDTO dto) throws JsonProcessingException {

            //Verificação de já existe uma appointment se não tiver retorna erro
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

            //Dando ‘update’ após passar em todas as verificações
            LocalDateTime start = dto.getDateTime();
            LocalDateTime end  = start.plusMinutes(30);
            List<Appointment> conflicts = repository.findConflicts(dto.getMedicId(), start, end);
            boolean hasConflict = conflicts.stream().anyMatch(a -> a.getId() != id);

            //Ve se a agenda retorna um conflito com alguma outra já existente no horário
            if(hasConflict)
                throw new IllegalArgumentException("Schedule already taken");

            existing.setMedicId(dto.getMedicId());
            existing.setPatientId(dto.getPatientId());
            existing.setDateTime(dto.getDateTime());
            existing.setObservation(dto.getObservation());
            existing.setStatus(AppointmentStatus.SCHEDULED);
            existing.setRoom(dto.getRoom());

            // 5. Notificacao via e-mail para patient
            PersonDto patientDto = entityClient.findPersonByIdToSendEmail(dto.getPatientId());

            appointmentProducer.publishAppointmentCreated(patientDto, existing);

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
