package com.ms.appointment.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
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
    private SqsTemplate sqsTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    //Nome da fila da Amazon SQS
    @Value(value = "${medcare.aws.sqs.queue.notification.email}")
    private String notificationQueue;

    public void publishAppointmentCreated(PersonDto personDto, Appointment appointment) throws JsonProcessingException {
        try{
            var emailDto = new EmailDto();

            emailDto.setEmailTo(personDto.getEmail());
            emailDto.setSubject("Nova consulta agendada");
            emailDto.setText("Nome: " + personDto.getNome() +
                    "\nEmail: " + personDto.getEmail() +
                    "\n----------------------------------" +
                    "\nData: " + appointment.getStartTime() + "/" + appointment.getEndTime() +
                    "\nRoom: " + appointment.getRoom() +
                    "\nObservation: " + appointment.getObservation());

            String json = objectMapper.writeValueAsString(emailDto);

            sqsTemplate.send(to -> to
                    .queue(notificationQueue)
                    .payload(json));
            System.out.println("Message of appointment created sent");
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            throw e;
        }

    }

    public void publishAppointmentUpdated(PersonDto personDto, Appointment appointment) throws JsonProcessingException {
        try{
            var emailDto = new EmailDto();

            emailDto.setEmailTo(personDto.getEmail());
            emailDto.setSubject("Consulta Atualizada");
            emailDto.setText("Nome: " + personDto.getNome() +
                    "\nEmail: " + personDto.getEmail() +
                    "\n----------------------------------" +
                    "\nSTATUS: " + appointment.getStatus() +
                    "\n----------------------------------" +
                    "\nData: " + appointment.getStartTime() + "/" + appointment.getEndTime() +
                    "\nRoom: " + appointment.getRoom() +
                    "\nObservation: " + appointment.getObservation());

            String json = objectMapper.writeValueAsString(emailDto);

            sqsTemplate.send(to -> to
                    .queue(notificationQueue)
                    .payload(json));
            System.out.println("Message of appointment updated sent");
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
