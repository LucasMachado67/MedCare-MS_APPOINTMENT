package com.ms.appointment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ms.appointment.dtos.ScheduleCreationDTO;
import com.ms.appointment.models.Schedule;
import com.ms.appointment.repository.ScheduleRepository;

@Service
public class ScheduleService{

    @Autowired
    private ScheduleRepository repository;
    @Autowired
    private MsPerfilService perfilService;

    public Schedule createSchedule(ScheduleCreationDTO dto){

        if(dto.startTime().isAfter(dto.endTime()) || dto.startTime().isEqual(dto.endTime())){
            throw new IllegalArgumentException("Start Time or End Time invalid");
        }

        // 2. Validação Externa (Médico Existe) - VAMOS FAZER NA PRÓXIMA ETAPA!
        if (!perfilService.validateMedicExists(dto.medicId())) {
            throw new RuntimeException("Médico não encontrado no ms_perfil.");
        }

        Schedule schedule = new Schedule(
            dto.medicId(),
            dto.startTime(),
            dto.endTime()
        );

        return repository.save(schedule);

    }
}
