package com.ms.appointment.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ms.appointment.dtos.EmailDto;
import com.ms.appointment.dtos.PersonDto;
import com.ms.appointment.models.Schedule;

@Component
public class ScheduleProducer {


    @Autowired
    private SqsTemplate sqsTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Value(value = "${medcare.aws.sqs.queue.notification.email}")
    private String notificationQueue;

    public void publishScheduleCreated(PersonDto personDto, Schedule schedule) throws JsonProcessingException {
        var emailDto = new EmailDto();
         emailDto.setEmailTo(personDto.getEmail());
         emailDto.setSubject("Nova agenda configurada");
         emailDto.setText(
                "Nome: " + personDto.getNome() +
                "\n----------------------------------" +
                "\nDia: " + schedule.getDayOfWeek() +
                "\nInício da Agenda: " + schedule.getStartTime() +
                "\nFinal da Agenda: " + schedule.getEndTime());
        
        String json = objectMapper.writeValueAsString(emailDto);

        sqsTemplate.send(to -> to
                .queue(notificationQueue)
                .payload(json));
        System.out.println("Message Sent");

    }
}
