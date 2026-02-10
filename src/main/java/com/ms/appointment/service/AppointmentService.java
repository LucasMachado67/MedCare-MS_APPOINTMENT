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

        if(dto.getStarTime().isBefore(LocalDateTime.now().minusSeconds(10)))
            throw new IllegalArgumentException("Hour selected can not be in the past");
            //Lembrar de quando tiver acesso ao banco colocar isso para evitar overlapping ALTER TABLE appointment
            // ADD CONSTRAINT no_overlap
            // EXCLUDE USING gist (
            //     medic_id WITH =,
            //     tstzrange(start_time, end_time) WITH &&
            // )
            // WHERE (status = 'SCHEDULED');
        if(dto.getStarTime().isEqual(dto.getEndTime()) || dto.getStarTime().isAfter(dto.getEndTime()))
            throw new IllegalArgumentException("StartTime can not be the same/lower than EndTime");

        // Verifica se o médico possui agenda configurada neste horário
        if (!scheduleService.isWithinSchedule(dto.getMedicId(), dto.getStarTime(), dto.getEndTime()))
            throw new IllegalArgumentException("Medic does not have schedule configured for the selected time");

        // 3️- Verifica se já existe outro agendamento nesse horário

        boolean hasConflicts = repository.existsConflict(dto.getMedicId(), dto.getStarTime(), dto.getEndTime());

        if (hasConflicts)
            throw new IllegalArgumentException("Medic does not have schedule configured for the selected time");

        // 4️- Cria a consulta
        Appointment appointment = new Appointment();
        appointment.setPatientId(dto.getPatientId());
        appointment.setMedicId(dto.getMedicId());
        appointment.setStartTime(dto.getStarTime());
        appointment.setEndTime(dto.getEndTime());
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

        if(foundAppointments.isEmpty())
            System.out.println("No appointments found for this patient");

        return foundAppointments;
    }

    public List<Appointment> findByMedic(long id){
        List<Appointment> foundAppointments = repository.findByMedicId(id);

        if(foundAppointments.isEmpty())
            System.out.println("No appointments found for this medic");

        return foundAppointments;

    }

    public Appointment updateAppointment(long id, AppointmentRequestDTO dto) throws JsonProcessingException {

            //Verificação de já existe uma appointment se não tiver retorna erro
            Appointment existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
            //Verifica se não está cancelada
            if(existing.getStatus() == AppointmentStatus.CANCELLED)
                throw new IllegalArgumentException("Appointment already canceled, can not be updated");
            //Verifica se o patient existe
            if (!entityClient.patientExists(dto.getPatientId()))
                throw new IllegalArgumentException("Pacient not found");
            //Verifica se o Medic existe
            if (!entityClient.medicExists(dto.getMedicId()))
                throw new IllegalArgumentException("Medic not found");
            //Verifica se StartTime está no futuro
            if(dto.getStarTime().isBefore(LocalDateTime.now().minusSeconds(10)))
                throw new IllegalArgumentException("Hour selected can not be in the past");
            //Verifica se StartTime é diferente de EndTime e é depois de EndTime
            if(dto.getStarTime().isEqual(dto.getEndTime()) || dto.getStarTime().isAfter(dto.getEndTime()))
                throw new IllegalArgumentException("StartTime can not be the same/lower than EndTime");
            //Verifica se o médico possuí uma agenda configurada para este horário
            if (!scheduleService.isWithinSchedule(dto.getMedicId(), dto.getStarTime(), dto.getEndTime()))
                throw new IllegalArgumentException("Medic does not have schedule configured for the selected time");
            //Vrifica se a appointment continua no mesmo horário
            if (repository.existsConflictExcludingId(dto.getMedicId(), dto.getStarTime(), dto.getEndTime(), id))
                throw new IllegalArgumentException("Schedule already taken");
            
            //Dando ‘update’ após passar em todas as verificações
            existing.setMedicId(dto.getMedicId());
            existing.setPatientId(dto.getPatientId());
            existing.setStartTime(dto.getStarTime());
            existing.setEndTime(dto.getEndTime());
            existing.setObservation(dto.getObservation());
            existing.setStatus(AppointmentStatus.SCHEDULED);
            existing.setRoom(dto.getRoom());

            // 5. Notificacao via e-mail para patient
            PersonDto patientDto = entityClient.findPersonByIdToSendEmail(dto.getPatientId());
            //Notifica o usuário sobre a nova Appointment via E-mail
            appointmentProducer.publishAppointmentUpdated(patientDto, existing);

            return repository.save(existing);
    }

    @Transactional
    public Appointment cancelAppointment(long id){
        Appointment existing = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        
        if(existing.getStatus() == AppointmentStatus.CANCELLED){
            throw new IllegalStateException("Appointment already cancelled");
        }
            
        existing.setStatus((AppointmentStatus.CANCELLED));
        repository.save(existing);
        return existing;
    }

    @Transactional
    public Appointment completeAppointment(long id){
        Appointment existing = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        if(existing.getStatus() != AppointmentStatus.IN_MEETING){
            throw new IllegalStateException("Only appointments in status IN_MEETING can be set as COMPLETED");
        }

        existing.setStatus(AppointmentStatus.COMPLETED);
        return repository.save(existing);
    }



}
