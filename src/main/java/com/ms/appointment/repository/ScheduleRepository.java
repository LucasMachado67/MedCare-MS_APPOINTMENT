package com.ms.appointment.repository;

import java.time.DayOfWeek;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ms.appointment.models.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>{

    Schedule findByMedicIdAndDayOfWeek(long medic_id, DayOfWeek dayOfWeek);

}
