package com.ms.appointment.repositoryTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.ms.appointment.enums.AppointmentStatus;
import com.ms.appointment.models.Appointment;
import com.ms.appointment.repository.AppointmentRepository;

@DataJpaTest
@ActiveProfiles("test")
public class AppointmentRepositoryTest {
    
    @Autowired
    private AppointmentRepository repository;

    @Autowired
    private TestEntityManager entityManager;
    
    Appointment appointment1;
    @BeforeEach
    void setup(){

        appointment1 = new Appointment();

        appointment1.setMedicId(1);
        appointment1.setPatientId(2);
        LocalDateTime start = LocalDateTime.of(2026, 2, 10, 10, 0);
        LocalDateTime end = start.plusMinutes(30);
        appointment1.setStartTime(start);
        appointment1.setEndTime(end);
        appointment1.setStatus(AppointmentStatus.SCHEDULED);
        appointment1.setDuration(30);
        appointment1.setRoom("Sala 500");
        appointment1.setCreatedAt(LocalDateTime.now());
        appointment1.setUpdatedAt(LocalDateTime.now());     
    }

    @Test
    void shouldReturnConflictWhenOverlapping() {

        entityManager.persist(appointment1);
        entityManager.flush();

        boolean result = repository.existsConflict(
                1,
                appointment1.getStartTime().plusMinutes(10),
                appointment1.getEndTime().plusMinutes(10)
        );

        assertTrue(result);
    }

    @Test
    void shouldNotReturnConflictOutsideRange() {

        entityManager.persist(appointment1);
        entityManager.flush();

        boolean result = repository.existsConflict(
                1,
                appointment1.getEndTime().plusMinutes(1),
                appointment1.getEndTime().plusMinutes(30)
        );

        assertFalse(result);
    }

    @Test
    void shouldNotReturnConflictForDifferentMedic() {

        appointment1.setMedicId(99);

        entityManager.persist(appointment1);
        entityManager.flush();

        boolean result = repository.existsConflict(
                1,
                appointment1.getStartTime(),
                appointment1.getEndTime()
        );

        assertFalse(result);
    }

}
