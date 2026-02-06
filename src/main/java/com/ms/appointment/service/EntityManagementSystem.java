package com.ms.appointment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.ms.appointment.dtos.PersonDto;

@Component
public class EntityManagementSystem {


    private final RestTemplate restTemplate;
    private final String entityServiceUrl;

    public EntityManagementSystem(RestTemplate restTemplate, @Value("${entity.service.url}") String entityServiceUrl){
        this.restTemplate = restTemplate;
        this.entityServiceUrl = entityServiceUrl;
    }

    public boolean patientExists(long patientId){
        try{
            restTemplate.getForEntity(
                entityServiceUrl + "/patient/" + patientId,
                Object.class
                );
            return true;
        }
        catch(HttpClientErrorException.NotFound e){
            System.out.println("Patient not found: " + e.getMessage());
            return false;
        }
    }

    public boolean medicExists(long medicId){
        try {
            restTemplate.getForEntity(
                entityServiceUrl + "/medic/" + medicId,
                Object.class
                );   
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            System.out.println("Medic not found: " + e.getMessage());
            return false;
        }
    }

    public PersonDto findPersonByIdToSendEmail(long id){
        try{
            ResponseEntity<PersonDto> response = restTemplate.getForEntity(
                entityServiceUrl + "/person/email/" + id,
                PersonDto.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                System.out.println("e-mail enviado para: " + response.getBody().getNome());
                return response.getBody(); 
            }
        }catch(RuntimeException e){
            System.out.println("Error while trying to get email: " + e.getMessage());
        }
        return null;
    }

}
