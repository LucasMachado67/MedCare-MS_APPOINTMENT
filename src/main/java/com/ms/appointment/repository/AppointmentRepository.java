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

    @Query("""
        SELECT COUNT(a) > 0
        FROM Appointment a
        WHERE a.medicId = :medicId
        AND a.status = 'SCHEDULED'
        AND a.startTime < :end
        AND a.endTime > :start
    """)
    boolean existsConflict(
            @Param("medicId") long medicId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );



    @Query("""
        SELECT COUNT(a) > 0
        FROM Appointment a
        WHERE a.medicId = :medicId
        AND a.status = 'SCHEDULED'
        AND a.startTime < :end
        AND a.endTime > :start
        AND a.id <> :appointmentId
    """)
    boolean existsConflictExcludingId(
            @Param("medicId") long medicId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("appointmentId") long appointmentId
    );

    

    @Query("SELECT a FROM Appointment a WHERE a.patientId = :patientId")
    List<Appointment> findByPatientId(@Param("patientId") long patientId);

    @Query("SELECT a FROM Appointment a WHERE a.medicId = :medicId")
    List<Appointment> findByMedicId(@Param("medicId") long medicId);
}
