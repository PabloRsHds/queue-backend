package br.com.queue.repositories.attendance;

import br.com.queue.entities.attendance.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, String> {

    Optional<Attendance> findByAttendanceId(String attendanceId);
}
