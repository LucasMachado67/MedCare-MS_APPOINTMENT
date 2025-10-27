package com.ms.appointment.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ms.appointment.models.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>{

    @Query("SELECT a FROM Appointment a WHERE a.medicId = :medicId AND " +
        "a.date BETWEEN :start AND :end AND a.status = 'SCHEDULED'")
    List<Appointment> findConflicts(@Param("medicId") long medicId, LocalDateTime start, LocalDateTime end);
    

    @Query("SELECT a FROM Appointment a WHERE a.patientId = :patientId")
    List<Appointment> findByPatientId(@Param("patientId") long patientId);

    @Query("SELECT a FROM Appointment a WHERE a.medicId = :medicId")
    List<Appointment> findByMedicId(@Param("medicId") long medicId);
}
