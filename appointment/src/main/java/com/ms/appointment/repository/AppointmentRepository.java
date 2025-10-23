package com.ms.appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ms.appointment.models.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>{

}
