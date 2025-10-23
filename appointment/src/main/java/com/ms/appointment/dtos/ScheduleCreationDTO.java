package com.ms.appointment.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record ScheduleCreationDTO(
    
    @NotNull(message = "The medic's id is necessary")
    Long medicId,

    @NotNull(message = "The start time is necessary")
    LocalDateTime startTime,
    
    @NotNull(message = "The end time is necessary")
    LocalDateTime endTime) {

    
}
