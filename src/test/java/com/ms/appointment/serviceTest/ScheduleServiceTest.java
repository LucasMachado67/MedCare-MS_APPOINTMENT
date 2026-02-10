package com.ms.appointment.serviceTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ms.appointment.dtos.PersonDto;
import com.ms.appointment.dtos.ScheduleCreationDTO;
import com.ms.appointment.models.Schedule;
import com.ms.appointment.producer.ScheduleProducer;
import com.ms.appointment.repository.ScheduleRepository;
import com.ms.appointment.service.EntityManagementSystem;
import com.ms.appointment.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {

    @Mock
    private ScheduleRepository repository;

    @Mock
    private EntityManagementSystem entityManagementSystem;

    @Mock
    private ScheduleProducer scheduleProducer;

    @InjectMocks
    private ScheduleService service;

    Schedule schedule;
    ScheduleCreationDTO dto;

    @BeforeEach
    void setup(){
        
        dto = new ScheduleCreationDTO();
        dto.setMedicId(2L);
        dto.setStartTime(LocalTime.parse("08:00:00"));
        dto.setEndTime(LocalTime.parse("12:00:00"));
        dto.setDayOfWeek(DayOfWeek.MONDAY);

        schedule = new Schedule();
        schedule.setId(1L);
        schedule.setMedicId(2L);
        schedule.setStartTime(LocalTime.parse("08:00:00"));
        schedule.setEndTime(LocalTime.parse("12:00:00"));
        schedule.setDayOfWeek(DayOfWeek.MONDAY);

    }


    // ===============================
    // CREATE SCHEDULE
    // ===============================
    @Nested
    class CreateSchedule{

        @Test
        void shouldCreateSchedule() throws JsonProcessingException {

            when(entityManagementSystem.medicExists(2L)).thenReturn(true);

            PersonDto medicDto = new PersonDto();
            when(entityManagementSystem.findPersonByIdToSendEmail(schedule.getMedicId())).thenReturn(medicDto);

            doNothing().when(scheduleProducer)
                    .publishScheduleCreated(any(), any());

            when(repository.save(any()))
                    .thenAnswer(inv -> inv.getArgument(0));

            Schedule result = service.save(dto);

            assertNotNull(result);
            assertEquals(2, result.getMedicId());
            assertEquals(DayOfWeek.MONDAY, result.getDayOfWeek());
        }

        @Test
        void shouldThrowWhenMedicDoesNotExists() throws JsonProcessingException {

            when(entityManagementSystem.medicExists(2L)).thenReturn(false);

            IllegalArgumentException exception =
                        assertThrows(IllegalArgumentException.class, () -> service.save(dto));

            assertEquals("Invalid medic ID", exception.getMessage());
            verify(scheduleProducer, never()).publishScheduleCreated(any(), any());
            verify(repository, never()).save(any());
        }
    }

    @Nested
    class WithinSchedule{

        @Test
        void shouldReturnFalseIfMedicDoesNotExists(){
            when(entityManagementSystem.medicExists(2L)).thenReturn(false);

            LocalDateTime startTime = LocalDateTime.now();
            LocalDateTime endTime = startTime.plusMinutes(30);

            assertFalse(service.isWithinSchedule(schedule.getMedicId(), startTime, endTime));
            verify(repository, never()).findByMedicIdAndDayOfWeek(anyLong(), any());
        }

        @Test
        void shouldReturnFalseIfScheduleIsNull(){
            when(entityManagementSystem.medicExists(2L)).thenReturn(true);

            LocalDateTime startTime = LocalDateTime.of(2026, 2, 9, 10, 0);
            LocalDateTime endTime = startTime.plusMinutes(30);

            doReturn(null)
                    .when(repository)
                    .findByMedicIdAndDayOfWeek(2L, startTime.getDayOfWeek());

            assertFalse(service.isWithinSchedule(schedule.getMedicId(), startTime, endTime));
        }

        @Test
        void shouldReturnTrueIfIsWithinSchedule(){

            LocalDateTime startTime = LocalDateTime.of(2026, 2, 9, 10, 0);
            LocalDateTime endTime = startTime.plusMinutes(30);

            when(entityManagementSystem.medicExists(schedule.getMedicId())).thenReturn(true);

            when(repository.findByMedicIdAndDayOfWeek(schedule.getMedicId(), startTime.getDayOfWeek()))
                    .thenReturn(schedule);

            boolean result = service.isWithinSchedule(schedule.getMedicId(), startTime, endTime);
            assertTrue(result);
        }

    }



}
