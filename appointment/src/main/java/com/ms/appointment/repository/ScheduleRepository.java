package com.ms.appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ms.appointment.models.Schedule;


@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>{


}
