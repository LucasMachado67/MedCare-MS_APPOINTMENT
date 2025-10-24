package com.ms.appointment.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ms.appointment.models.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>{

    @Query("SELECT a FROM Appointment a WHERE a.medicId = :medicId AND " +
        "a.dateTime BETWEEN :start AND :end AND a.status = 'SCHEDULED'")
    List<Appointment> findConflicts(long medicId, LocalDateTime start, LocalDateTime end);
    
}
