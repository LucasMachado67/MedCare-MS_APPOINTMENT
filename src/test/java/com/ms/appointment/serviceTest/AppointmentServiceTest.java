package com.ms.appointment.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ms.appointment.dtos.AppointmentRequestDTO;
import com.ms.appointment.dtos.PersonDto;
import com.ms.appointment.enums.AppointmentStatus;
import com.ms.appointment.models.Appointment;
import com.ms.appointment.producer.AppointmentProducer;
import com.ms.appointment.repository.AppointmentRepository;
import com.ms.appointment.service.AppointmentService;
import com.ms.appointment.service.EntityManagementSystem;
import com.ms.appointment.service.ScheduleService;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository repository;

    @Mock
    private EntityManagementSystem entityClient;

    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private AppointmentService service;

    @Mock
    private AppointmentProducer appointmentProducer;

    private AppointmentRequestDTO appointmentDTO;

    Appointment appointment1;
    Appointment appointment2;

    @BeforeEach
    void setup(){
        appointmentDTO = new AppointmentRequestDTO();
        appointmentDTO.setMedicId(1L);
        appointmentDTO.setPatientId(20L);
        appointmentDTO.setStarTime(LocalDateTime.now());
        appointmentDTO.setEndTime(LocalDateTime.now().plusMinutes(30));
        appointmentDTO.setRoom("Sala 101");
        appointmentDTO.setObservation("Retorno");

        appointment1 = new Appointment();
        appointment1.setPatientId(1);
        appointment1.setMedicId(2);

        appointment2 = new Appointment();
        appointment2.setPatientId(1);
        appointment2.setMedicId(2);
    }


    // ====================================
    // CREATE APPOINTMENT
    // ====================================
    @Nested
    class createAppointment{

        @Test
        void shouldCreateAppointment() throws Exception {
    
            // Arrange
            when(entityClient.medicExists(1L)).thenReturn(true);
            when(entityClient.patientExists(20L)).thenReturn(true);
            when(scheduleService.isWithinSchedule(1L, appointmentDTO.getStarTime(), appointmentDTO.getEndTime())).thenReturn(true);
    
            when(repository.existsConflict(anyLong(), any(), any()))
                    .thenReturn(false);
    
            // mock PersonDto
            PersonDto person = new PersonDto();
            when(entityClient.findPersonByIdToSendEmail(anyLong()))
                    .thenReturn(person);
    
            when(repository.save(any()))
                    .thenAnswer(inv -> inv.getArgument(0));
    
            // Act
            Appointment result = service.createAppointment(appointmentDTO);
    
            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getMedicId());
            assertEquals(20L, result.getPatientId());
        }
    
        @Test
        void shouldThrowWhenMedicDoesNotExists() throws Exception {
    
            // Arrange
            when(entityClient.medicExists(1L)).thenReturn(false);
            
    
            IllegalArgumentException exception = 
                assertThrows(IllegalArgumentException.class, () -> service.createAppointment(appointmentDTO));
    
            assertEquals("Medic not found", exception.getMessage());
        }
    
        @Test
        void shouldThrowWhenPatientDoesNotExists() throws Exception {
    
            // Arrange
            when(entityClient.medicExists(1L)).thenReturn(true);
            when(entityClient.patientExists(20L)).thenReturn(false);
            
    
            IllegalArgumentException exception = 
                assertThrows(IllegalArgumentException.class, () -> service.createAppointment(appointmentDTO));
    
            assertEquals("Patient not found", exception.getMessage());
        }
    
        @Test
        void shouldThrowWhenScheduleIsInThePast() throws Exception {
    
            // Arrange
            when(entityClient.medicExists(1L)).thenReturn(true);
            when(entityClient.patientExists(20L)).thenReturn(true);
    
            appointmentDTO.setStarTime(LocalDateTime.now().minusHours(1));
            appointmentDTO.setEndTime(LocalDateTime.now().minusMinutes(30));
    
            IllegalArgumentException exception = 
                assertThrows(IllegalArgumentException.class, () -> service.createAppointment(appointmentDTO));
    
            assertEquals("Hour selected is invalid", exception.getMessage());
        }
    
        @Test
        void shouldThrowWhenStartTimeEqualsEndTime() {
    
            LocalDateTime time = LocalDateTime.now().plusHours(1);
    
            appointmentDTO.setStarTime(time);
            appointmentDTO.setEndTime(time);
    
            when(entityClient.medicExists(1L)).thenReturn(true);
            when(entityClient.patientExists(20L)).thenReturn(true);
    
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class,
                            () -> service.createAppointment(appointmentDTO));
    
            assertEquals("Hour selected is invalid", exception.getMessage());
        }
    
        @Test
        void shouldThrowWhenStartIsAfterEnd() {
    
            LocalDateTime start = LocalDateTime.now().plusHours(2);
            LocalDateTime end = LocalDateTime.now().plusHours(1);
    
            appointmentDTO.setStarTime(start);
            appointmentDTO.setEndTime(end);
    
            when(entityClient.medicExists(1L)).thenReturn(true);
            when(entityClient.patientExists(20L)).thenReturn(true);
    
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class,
                            () -> service.createAppointment(appointmentDTO));
    
            assertEquals("Hour selected is invalid", exception.getMessage());
        }
    
    
    
        @Test
        void shouldThrowWhenMedicDoesNotHaveScheduleConfigured() throws Exception {
    
            // Arrange
            when(entityClient.medicExists(1L)).thenReturn(true);
            when(entityClient.patientExists(20L)).thenReturn(true);
    
            when(scheduleService.isWithinSchedule(1L, appointmentDTO.getStarTime(), appointmentDTO.getEndTime())).thenReturn(false);
            
    
            IllegalArgumentException exception = 
                assertThrows(IllegalArgumentException.class, () -> service.createAppointment(appointmentDTO));
    
            assertEquals("Medic does not have schedule configured for the selected time", exception.getMessage());
        }
    
        @Test
        void shouldThrowWhenScheduleIsFullfilled() throws Exception {
    
            // Arrange
            when(entityClient.medicExists(1L)).thenReturn(true);
            when(entityClient.patientExists(20L)).thenReturn(true);
            when(scheduleService.isWithinSchedule(1L, appointmentDTO.getStarTime(), appointmentDTO.getEndTime())).thenReturn(true);
            
            List<Appointment> lista = new ArrayList<>();
            lista.add(appointment1);
    
            when(repository.existsConflict(anyLong(), any(), any()))
                    .thenReturn(true);
    
            IllegalArgumentException exception = 
                assertThrows(IllegalArgumentException.class, () -> service.createAppointment(appointmentDTO));
    
            assertEquals("Medic does not have schedule configured for the selected time", exception.getMessage());
        }
    }


    // ====================================
    // FindBy APPOINTMENT
    // ====================================
    @Nested
    class FindByAppointment{

        @Test
        void shouldReturnAppointmentWhenSearchByPatient(){
    
            // Arrange
            List<Appointment> appointments = List.of(appointment1, appointment2);
    
            when(repository.findByPatientId(appointment1.getPatientId()))
                    .thenReturn(appointments);
    
            // Act
            List<Appointment> lista = service.findByPatient(appointment1.getPatientId());
    
            assertEquals(2, lista.size());
            verify(repository).findByPatientId(appointment1.getPatientId());
        }
    
        @Test
        void shouldReturnAppointmentWhenSearchByMedic(){
    
            // Arrange
            List<Appointment> appointments = List.of(appointment1,appointment2);
    
            when(repository.findByMedicId(appointment1.getMedicId()))
                    .thenReturn(appointments);
    
            // Act
            List<Appointment> lista = service.findByMedic(appointment1.getMedicId());
    
            assertEquals(2, lista.size());
            verify(repository).findByMedicId(appointment1.getMedicId());
        }
    }

    // ====================================
    // CANCEL APPOINTMENT
    // ====================================

    @Nested
    class cancelAppointment {

        @Test
        void shouldCancelAppointment(){
            Appointment appointment = new Appointment();
            appointment.setId(1);
            appointment.setStatus(AppointmentStatus.SCHEDULED);
    
            when(repository.findById(1L))
                    .thenReturn(Optional.of(appointment));
    
            service.cancelAppointment(1L);
    
            assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
            verify(repository).save(appointment);
        }
    
        @Test
        void shouldThrowWhenAppointmentNotFound() {
    
            when(repository.findById(1L))
                    .thenReturn(Optional.empty());
    
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class,
                            () -> service.cancelAppointment(1L));
    
            assertEquals("Appointment not found", exception.getMessage());
        }
    
        @Test
        void shouldKeepCancelledWhenAlreadyCancelled() {
    
            Appointment appointment = new Appointment();
            appointment.setId(1L);
            appointment.setStatus(AppointmentStatus.CANCELLED);
    
            when(repository.findById(1L))
                    .thenReturn(Optional.of(appointment));
    
            service.cancelAppointment(1L);
    
            assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
            verify(repository).save(appointment);
        }
    }

    // ====================================
    // UPDATE APPOINTMENT
    // ====================================
    @Nested
    class UpdateAppointment{
        @Test
        void shouldThrowIfAppointmentDoesNotExists() {
    
            Appointment appointment = new Appointment();
            appointment.setId(1L);
    
            when(repository.findById(1L))
                    .thenReturn(Optional.empty());
            
            IllegalArgumentException exception = 
                    assertThrows(IllegalArgumentException.class, () -> service.updateAppointment(1L, appointmentDTO));
    
            assertEquals("Appointment not found", exception.getMessage());
        }

        @Test
        void shouldThrowIfAppointmentIsCancelled() {
    
            Appointment appointment = new Appointment();
            appointment.setId(1L);
            appointment.setStatus(AppointmentStatus.CANCELLED);
            
            when(repository.findById(1L))
                    .thenReturn(Optional.of(appointment));
            
            IllegalArgumentException exception = 
                    assertThrows(IllegalArgumentException.class, () -> service.updateAppointment(1L, appointmentDTO));
    
            assertEquals("Appointment already canceled, can not be updated", exception.getMessage());
        }

        @Test
        void shouldThrowIfPatientDoesNotExists() {
    
            Appointment appointment = new Appointment();
            appointment.setId(1L);
            appointment.setStatus(AppointmentStatus.SCHEDULED);
            
            when(repository.findById(1L))
                    .thenReturn(Optional.of(appointment));

            when(entityClient.patientExists(20L)).thenReturn(false);
            
            IllegalArgumentException exception = 
                    assertThrows(IllegalArgumentException.class, () -> service.updateAppointment(1L, appointmentDTO));
    
            assertEquals("Pacient not found", exception.getMessage());
        }

        @Test
        void shouldThrowIfMedicDoesNotExists() {
    
            Appointment appointment = new Appointment();
            appointment.setId(1L);
            appointment.setStatus(AppointmentStatus.SCHEDULED);
            
            when(repository.findById(1L))
                    .thenReturn(Optional.of(appointment));

            when(entityClient.patientExists(20L)).thenReturn(true);
            when(entityClient.medicExists(1L)).thenReturn(false);
            
            IllegalArgumentException exception = 
                    assertThrows(IllegalArgumentException.class, () -> service.updateAppointment(1L, appointmentDTO));
    
            assertEquals("Medic not found", exception.getMessage());
        }

        @Test
        void shouldThrowWhenScheduleIsInThePast() throws Exception {
    
            // Arrange
            Appointment appointment = new Appointment();
            appointment.setId(1L);
            appointment.setStatus(AppointmentStatus.SCHEDULED);
            
            when(repository.findById(1L))
                    .thenReturn(Optional.of(appointment));
            when(entityClient.medicExists(1L)).thenReturn(true);
            when(entityClient.patientExists(20L)).thenReturn(true);
    
            appointmentDTO.setStarTime(LocalDateTime.now().minusHours(1));
            appointmentDTO.setEndTime(LocalDateTime.now().minusMinutes(30));
    
            IllegalArgumentException exception = 
                assertThrows(IllegalArgumentException.class, () -> service.updateAppointment(1L,appointmentDTO));
            assertEquals("Hour selected is invalid", exception.getMessage());
        }
    
        @Test
        void shouldThrowWhenStartTimeEqualsEndTime() {
    
            LocalDateTime time = LocalDateTime.now().plusHours(1);
            
            appointmentDTO.setStarTime(time);
            appointmentDTO.setEndTime(time);
            
            Appointment appointment = new Appointment();
            appointment.setId(1L);
            appointment.setStatus(AppointmentStatus.SCHEDULED);
            
            when(repository.findById(1L))
                    .thenReturn(Optional.of(appointment));
            when(entityClient.medicExists(1L)).thenReturn(true);
            when(entityClient.patientExists(20L)).thenReturn(true);
    
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class,
                            () -> service.updateAppointment(1L,appointmentDTO));
    
            assertEquals("Hour selected is invalid", exception.getMessage());
        }
    
        @Test
        void shouldThrowWhenStartIsAfterEnd() {
    
            LocalDateTime start = LocalDateTime.now().plusHours(2);
            LocalDateTime end = LocalDateTime.now().plusHours(1);
    
            appointmentDTO.setStarTime(start);
            appointmentDTO.setEndTime(end);
            
            Appointment appointment = new Appointment();
            appointment.setId(1L);
            appointment.setStatus(AppointmentStatus.SCHEDULED);
            
            when(repository.findById(1L))
                    .thenReturn(Optional.of(appointment));
            when(entityClient.medicExists(1L)).thenReturn(true);
            when(entityClient.patientExists(20L)).thenReturn(true);
    
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class,
                            () -> service.updateAppointment(1L,appointmentDTO));
    
            assertEquals("Hour selected is invalid", exception.getMessage());
        }
    
    
    
        @Test
        void shouldThrowWhenMedicDoesNotHaveScheduleConfigured() throws Exception {
    
            // Arrange
            Appointment appointment = new Appointment();
            appointment.setId(1L);
            appointment.setStatus(AppointmentStatus.SCHEDULED);
            
            when(repository.findById(1L))
                    .thenReturn(Optional.of(appointment));
            when(entityClient.medicExists(1L)).thenReturn(true);
            when(entityClient.patientExists(20L)).thenReturn(true);
    
            when(scheduleService.isWithinSchedule(1L, appointmentDTO.getStarTime(), appointmentDTO.getEndTime())).thenReturn(false);
            
    
            IllegalArgumentException exception = 
                assertThrows(IllegalArgumentException.class, () -> service.updateAppointment(1L,appointmentDTO));
    
            assertEquals("Medic does not have schedule configured for the selected time", exception.getMessage());
        }

        @Test
        void shouldThrowWhenScheduleIsAlreadyTaken() throws Exception {
    
            // Arrange
            Appointment appointment = new Appointment();
            appointment.setId(1L);
            appointment.setStatus(AppointmentStatus.SCHEDULED);

            //DTO para Simular os novos dados para o UPDATE
            AppointmentRequestDTO dto = appointmentDTO;

            when(repository.findById(1L))
                    .thenReturn(Optional.of(appointment));
            when(entityClient.medicExists(1L)).thenReturn(true);
            when(entityClient.patientExists(20L)).thenReturn(true);
    
            when(scheduleService.isWithinSchedule(1L, appointmentDTO.getStarTime(), appointmentDTO.getEndTime())).thenReturn(true);
            
            when(repository.existsConflictExcludingId(1L, dto.getStarTime(), dto.getEndTime(), 1L)).thenReturn(true);
    
            IllegalArgumentException exception = 
                assertThrows(IllegalArgumentException.class, () -> service.updateAppointment(1L,appointmentDTO));
    
            assertEquals("Schedule already taken", exception.getMessage());
        }

        @Test
        void shouldUpdateAppointment() throws Exception {
    
            // Arrange
            Appointment appointment = new Appointment();
            appointment.setId(1L);
            appointment.setStatus(AppointmentStatus.SCHEDULED);

            //DTO para Simular os novos dados para o UPDATE
            AppointmentRequestDTO dto = appointmentDTO;

            when(repository.findById(1L))
                    .thenReturn(Optional.of(appointment));
            when(entityClient.medicExists(1L)).thenReturn(true);
            when(entityClient.patientExists(20L)).thenReturn(true);
    
            when(scheduleService.isWithinSchedule(1L, appointmentDTO.getStarTime(), appointmentDTO.getEndTime())).thenReturn(true);
            
            when(repository.existsConflictExcludingId(1L, dto.getStarTime(), dto.getEndTime(), 1L)).thenReturn(false);
            
            when(repository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

            Appointment result = service.updateAppointment(1L, dto);

            assertNotNull(result);
            assertEquals(1L, result.getMedicId());
            assertEquals(20L, result.getPatientId());
            assertEquals("Retorno", result.getObservation());
        }
    }

}