package br.com.queue.repositories.attendance;

import br.com.queue.dtos.attendance.statistics.GetAttendanceStatisticsDto;
import br.com.queue.entities.attendance.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, String> {

    Optional<Attendance> findByAttendanceId(String attendanceId);

    @Query(value = """
        SELECT
            COUNT(*) FILTER (WHERE t.status = 'WAITING') AS countAttendancesWaiting,
            COUNT(*) FILTER (WHERE t.status = 'IN_PROGRESS') AS countAttendancesInProgress,
            COUNT(*) FILTER (WHERE t.status = 'FINISHED' AND DATE(a.started_at) = CURRENT_DATE) AS countAttendancesOfDay,
            AVG(EXTRACT(EPOCH FROM (a.started_at - t.created_at))) FILTER (WHERE a.started_at IS NOT NULL AND t.created_at IS NOT NULL) AS averageWaitingTime,
            AVG(EXTRACT(EPOCH FROM (a.finished_at - a.started_at))) FILTER (WHERE a.started_at IS NOT NULL AND a.finished_at IS NOT NULL) AS averageServiceTime
        FROM tb_attendances a
        RIGHT JOIN tb_tickets t ON a.ticket_id = t.ticket_id
    """, nativeQuery = true)
    GetAttendanceStatisticsDto getAttendanceStatistics();
}
