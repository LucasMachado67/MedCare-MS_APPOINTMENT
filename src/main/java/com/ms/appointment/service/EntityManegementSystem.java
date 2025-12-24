package com.ms.appointment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException.NotFound;

import com.ms.appointment.dtos.PersonDto;

@Component
public class EntityManegementSystem {


    private final RestTemplate restTemplate;
    private final String entityServiceUrl;

    public EntityManegementSystem(RestTemplate restTemplate, @Value("${entity.service.url}") String entityServiceUrl){
        this.restTemplate = restTemplate;
        this.entityServiceUrl = entityServiceUrl;
    }

    public boolean patientExists(long patientId){
        try{
            restTemplate.getForEntity(
                entityServiceUrl + "/patients/" + patientId,
                Object.class
                );
            return true;
        }
        catch(HttpClientErrorException.NotFound e){
            return false;
        }
    }

    public boolean medicExists(long medicId){
        try {
            restTemplate.getForEntity(
                entityServiceUrl + "/medics/" + medicId,
                Object.class
                );   
            return true;
        } catch (HttpClientErrorException.NotFound e) {

            return false;
        }
    }

    public PersonDto findPersonByIdToSendEmail(long id){
        try{
            ResponseEntity<PersonDto> response = restTemplate.getForEntity(
                entityServiceUrl + "/person/email/" + id,  // caminho correto do endpoint
                PersonDto.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody(); 
            } else {
                throw new RuntimeException("Falha ao obter dados da pessoa (HTTP " + response.getStatusCode() + ")");
            }
        }catch(NotFound e){
            e.printStackTrace();
        }
        return null;
    }

}
