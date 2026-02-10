package com.ms.appointment.ControllerTest;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.appointment.controller.ScheduleController;
import com.ms.appointment.dtos.ScheduleCreationDTO;
import com.ms.appointment.exception.GlobalExceptionHandler;
import com.ms.appointment.models.Schedule;
import com.ms.appointment.service.ScheduleService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;


@Import(GlobalExceptionHandler.class)
@WebMvcTest(ScheduleController.class)
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScheduleService service;

    @Autowired
    private ObjectMapper mapper;
    ScheduleCreationDTO dto;
    Schedule schedule;
    Schedule schedule2;

    @BeforeEach
    void setup(){

        dto = new ScheduleCreationDTO();
        dto.setMedicId(2L);
        dto.setStartTime(LocalTime.now());
        dto.setEndTime(LocalTime.now().plusMinutes(30));
        dto.setDayOfWeek(DayOfWeek.MONDAY);
        schedule =  new Schedule();
        schedule.setMedicId(2L);
        schedule.setStartTime(LocalTime.now());
        schedule.setEndTime(LocalTime.now().plusMinutes(30));
        schedule.setDayOfWeek(DayOfWeek.THURSDAY);
        
        schedule2 =  new Schedule();
    }

    @Nested
    class CreateSchedule{


        @Test
        void shouldCreateSchedule() throws Exception{

            when(service.save(any())).thenReturn(schedule);

            mockMvc.perform(post("/schedule/create")
                            .with(jwt())
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.medicId").value(2L));
        }

        @Test
        void shouldThrowIfScheduleIsInvalid() throws Exception{

            ScheduleCreationDTO dto2 = new ScheduleCreationDTO();

            mockMvc.perform(post("/schedule/create")
                            .with(jwt())
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto2)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldThrowIfMedicDoesNotExists() throws Exception{

            when(service.save(any()))
                .thenThrow(new IllegalArgumentException("Invalid medic ID"));

            mockMvc.perform(post("/schedule/create")
                            .with(jwt())
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class FindBySchedule{

        @Test
        void shouldFindScheduleByMedicId() throws Exception{

            when(service.findByMedicId(anyLong())).thenReturn(List.of(schedule));

            mockMvc.perform(post("/schedule/1")
                            .with(jwt())
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                            .andExpect(jsonPath("$[0].medicId").value(2L))
                            .andExpect(jsonPath("$", hasSize(1)));
        }
    }
}
