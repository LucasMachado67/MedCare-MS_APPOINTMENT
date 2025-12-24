package com.ms.appointment.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ms.appointment.dtos.EmailDto;
import com.ms.appointment.dtos.PersonDto;
import com.ms.appointment.models.Schedule;

@Component
public class ScheduleProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value(value = "${broker.queue.email.name}")//Default exchange type
    private String routingKey;

    public void publishScheduleCreated(PersonDto personDto, Schedule schedule){
        var emailDto = new EmailDto();
        
         emailDto.setEmailTo(personDto.getEmail());
         emailDto.setSubject("Nova agenda configurada");
         emailDto.setText(
                "Nome: " + personDto.getNome() +
                "\n----------------------------------" +
                "\nDia: " + schedule.getDayOfWeek() +
                "\nInício da Agenda: " + schedule.getStartTime() +
                "\nFinal da Agenda: " + schedule.getEndTime());
        
            

        rabbitTemplate.convertAndSend("", routingKey, emailDto);
    }
}
