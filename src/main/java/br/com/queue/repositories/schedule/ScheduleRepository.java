package br.com.queue.repositories.schedule;

import br.com.queue.dtos.schedule.allSchedules.ResponseAllSchedulesDto;
import br.com.queue.entities.schedule.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {

    Optional<Schedule> findByScheduleId(String scheduleId);

    @Query(value = """
    SELECT
        s.schedule_id AS scheduleId,
        c.customer_id AS customerId,
        c.name AS customerName,
        sm.service_management_id AS serviceManagementId,
        sm.name AS serviceManagementName,
        s.scheduled_date AS scheduleDate,
        s.status AS status
    FROM tb_schedules s
    INNER JOIN tb_customers c
        ON c.customer_id = s.customer_id
    INNER JOIN tb_service_management sm
        ON sm.service_management_id = s.service_management_id
    WHERE (
        :search IS NULL
        OR :search = ''
        OR UNACCENT(LOWER(c.name))
            LIKE UNACCENT(LOWER(CONCAT('%', :search, '%')))
        OR UNACCENT(LOWER(sm.name))
            LIKE UNACCENT(LOWER(CONCAT('%', :search, '%')))
        OR TO_CHAR(s.scheduled_date, 'DD/MM/YYYY HH24:MI')
            LIKE CONCAT('%', :search, '%')
    )
    ORDER BY COALESCE(s.updated_at, s.created_at) DESC
    """,
            countQuery = """
    SELECT COUNT(*)
    FROM tb_schedules s
    INNER JOIN tb_customers c
        ON c.customer_id = s.customer_id
    INNER JOIN tb_service_management sm
        ON sm.service_management_id = s.service_management_id
    WHERE (
        :search IS NULL
        OR :search = ''
        OR UNACCENT(LOWER(c.name))
            LIKE UNACCENT(LOWER(CONCAT('%', :search, '%')))
        OR UNACCENT(LOWER(sm.name))
            LIKE UNACCENT(LOWER(CONCAT('%', :search, '%')))
        OR TO_CHAR(s.scheduled_date, 'DD/MM/YYYY HH24:MI')
            LIKE CONCAT('%', :search, '%')
    )
    """,
            nativeQuery = true
    )
    Page<ResponseAllSchedulesDto> findAllWithSearch(@Param("search") String search, Pageable pageable);
}
