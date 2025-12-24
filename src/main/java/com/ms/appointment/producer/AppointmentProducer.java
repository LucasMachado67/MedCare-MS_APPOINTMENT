package com.ms.appointment.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import com.ms.appointment.dtos.EmailDto;
import com.ms.appointment.dtos.PersonDto;
import com.ms.appointment.models.Appointment;

@Component
@ComponentScan
public class AppointmentProducer {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value(value = "${broker.queue.email.name}")//Default exchange type
    private String routingKey;

    public void publishAppointentCreated(PersonDto personDto, Appointment appointment){
        var emailDto = new EmailDto();
        
         emailDto.setEmailTo(personDto.getEmail());
         emailDto.setSubject("Nova consulta agendada");
         emailDto.setText("Nome: " + personDto.getNome() +
                "\nEmail: " + personDto.getEmail() +
                "\n----------------------------------" +
                "\nData: " + appointment.getDate() +
                "\nRoom: " + appointment.getRoom() +
                "\nObservation: " + appointment.getObservation());
        
            

        rabbitTemplate.convertAndSend("", routingKey, emailDto);
    }

    public void publishAppointentUpdated(PersonDto personDto, Appointment appointment){
        var emailDto = new EmailDto();
        
         emailDto.setEmailTo(personDto.getEmail());
         emailDto.setSubject("Consulta Atualizada");
         emailDto.setText("Nome: " + personDto.getNome() +
                "\nEmail: " + personDto.getEmail() +
                "\n----------------------------------" +
                "\nSTATUS: " + appointment.getStatus() + 
                "\n----------------------------------" +
                "\nData: " + appointment.getDate() +
                "\nRoom: " + appointment.getRoom() +
                "\nObservation: " + appointment.getObservation());
        
            

        rabbitTemplate.convertAndSend("", routingKey, emailDto);
    }
}
