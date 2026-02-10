package com.ms.appointment.repository;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ms.appointment.models.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>{

    Schedule findByMedicIdAndDayOfWeek(long medic_id, DayOfWeek dayOfWeek);


    @Query("SELECT s FROM Schedule s WHERE s.medicId = :medicId")
    List<Schedule> findByMedicId(@Param("medicId") long medicId);
}
