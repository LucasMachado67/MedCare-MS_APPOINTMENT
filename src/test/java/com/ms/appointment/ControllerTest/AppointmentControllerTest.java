package com.ms.appointment.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.appointment.controller.AppointmentController;
import com.ms.appointment.dtos.AppointmentRequestDTO;
import com.ms.appointment.exception.GlobalExceptionHandler;
import com.ms.appointment.models.Appointment;
import com.ms.appointment.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;
import java.util.List;

@Import(GlobalExceptionHandler.class)
@WebMvcTest(AppointmentController.class)
public class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentService service;

    @Autowired
    private ObjectMapper mapper;
    AppointmentRequestDTO dto;
    Appointment appointment;
    Appointment appointment2;

    @BeforeEach
    void setup(){
        // Arrange
        dto = new AppointmentRequestDTO();
        dto.setMedicId(1L);
        dto.setPatientId(20L);
        dto.setStarTime(LocalDateTime.now().plusHours(1));
        dto.setEndTime(LocalDateTime.now().plusHours(1).plusMinutes(30));
        dto.setObservation("Consulta");
        dto.setRoom("101");

        appointment =  new Appointment();
        appointment.setId(1L);
        appointment.setMedicId(1L);
        appointment.setPatientId(20L);

        appointment2 =  new Appointment();
        appointment2.setId(2L);
        appointment2.setMedicId(1L);
        appointment2.setPatientId(20L);
    }

    @Nested
    class createAppointment{
        @Test
        void shouldCreateAppointment() throws  Exception{

            when(service.createAppointment(any())).thenReturn(appointment);

            mockMvc.perform(post("/appointment/create")
                            .with(jwt())
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.medicId").value(1L))
                    .andExpect(jsonPath("$.patientId").value(20L));
        }

        @Test
        void shouldReturnBadRequestWhenDtoIsInvalid() throws Exception {

            AppointmentRequestDTO dto2 = new AppointmentRequestDTO();

            mockMvc.perform(post("/appointment/create")
                            .with(jwt())
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto2)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnBadRequestWhenMedicDoesNotExist() throws Exception {

            when(service.createAppointment(any()))
                    .thenThrow(new IllegalArgumentException("Medic not found"));

            mockMvc.perform(post("/appointment/create")
                            .with(jwt())
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnBadRequestWhenPatientDoesNotExist() throws Exception {

            when(service.createAppointment(any()))
                    .thenThrow(new IllegalArgumentException("Patient not found"));

            mockMvc.perform(post("/appointment/create")
                            .with(jwt())
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());

        }

        @Test
        void shouldReturnBadRequestWhenAppointmentIsInThePast() throws Exception {

            when(service.createAppointment(any()))
                    .thenThrow(new IllegalArgumentException("Hour selected can not be in the past"));

            mockMvc.perform(post("/appointment/create")
                            .with(jwt())
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnBadRequestWhenMedicDoesNotHaveAppointmentConfigured() throws Exception {

            when(service.createAppointment(any()))
                    .thenThrow(new IllegalArgumentException("Medic does not have schedule configured for the selected time"));

            mockMvc.perform(post("/appointment/create")
                            .with(jwt())
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnBadRequestWhenStartTimeIsTheSameOrLowerThenEndTime() throws Exception {

            when(service.createAppointment(any()))
                    .thenThrow(new IllegalArgumentException("StartTime can not be the same/lower than EndTime"));

            mockMvc.perform(post("/appointment/create")
                            .with(jwt())
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

    }

    @Nested
    class FindBy{

        @Test
        void shouldReturnAppointmentsByPatient() throws Exception {

            when(service.findByPatient(anyLong())).thenReturn(List.of(appointment, appointment2));

            mockMvc.perform(get("/appointment/patient/20")
                            .with(jwt()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].patientId").value(appointment.getPatientId()))
                    .andExpect(jsonPath("$[1].patientId").value(appointment2.getPatientId()));
        }

        @Test
        void shouldReturnAppointmentsByMedic() throws Exception {

            when(service.findByMedic(anyLong())).thenReturn(List.of(appointment, appointment2));

            mockMvc.perform(get("/appointment/medic/1")
                            .with(jwt()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].medicId").value(appointment.getMedicId()))
                    .andExpect(jsonPath("$[1].medicId").value(appointment2.getMedicId()));
        }
    }
}
