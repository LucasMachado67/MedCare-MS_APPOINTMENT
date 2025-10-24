package com.ms.appointment.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EntityManegementSystem {


    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080";

    public boolean isPatientActive(long patient_id){
        try {
            ResponseEntity<Boolean> response = restTemplate.getForEntity(
                BASE_URL + "patients" + patient_id, Boolean.class);
            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isMedicActive(long medic_id){
        try {
            ResponseEntity<Boolean> response = restTemplate.getForEntity(
                BASE_URL + "medics" + medic_id, Boolean.class);
            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            return false;
        }
    }

}
