package br.com.queue.repositories.serviceManagement;

import br.com.queue.dto.serviceManagement.ResponseServiceManagementDto;
import br.com.queue.dto.serviceManagement.statistics.ResponseStatisticsDto;
import br.com.queue.entities.serviceManagement.ServiceManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface ServiceManagementRepository extends JpaRepository<ServiceManagement, String> {

    // Importação de extenção para acentuações no sql
    // CREATE EXTENSION IF NOT EXISTS unaccent;

    Optional<ServiceManagement> findByServiceManagementId(String serviceManagementId);

    @Query(value = """
            SELECT s
            FROM tb_service_management s
            WHERE s.service_management_id IN :serviceManagementIds
        """, nativeQuery = true)
    Set<ServiceManagement> findAllByServiceManagementIdIn(
            @Param("serviceManagementIds") Set<String> serviceManagementIds);

    @Query(value = """
        SELECT
            s.service_management_id AS serviceManagementId,
            s.name AS name,
            s.code AS code,
            s.description AS description,
            d.department_id AS departmentId,
            d.name AS departmentName,
            s.active AS active
        FROM tb_service_management s
        INNER JOIN tb_departments d
            ON d.department_id = s.department_id
        WHERE (
            :search IS NULL
            OR :search = ''
            OR unaccent(LOWER(s.name))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
            OR unaccent(LOWER(s.code))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
            OR unaccent(LOWER(d.name))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
        )
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM tb_service_management s
        INNER JOIN tb_departments d
            ON d.department_id = s.department_id
        WHERE (
            :search IS NULL
            OR :search = ''
            OR unaccent(LOWER(s.name))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
            OR unaccent(LOWER(s.code))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
            OR unaccent(LOWER(d.name))
                LIKE unaccent(LOWER(CONCAT('%', :search, '%')))
        )
        """,
            nativeQuery = true
    )
    Page<ResponseServiceManagementDto> findAllWithSearch(@Param("search") String search, Pageable pageable);

    @Query(value = """
            SELECT
                COUNT(*) AS totalElements,
                COUNT(*) FILTER (WHERE active = true) AS totalElementsActive,
                COUNT(*) FILTER (WHERE active = false) AS totalElementsInactive,
                ROUND(
                    (
                        COUNT(*) FILTER (WHERE active = true)::numeric
                        / NULLIF(COUNT(*), 0)
                    ) * 100,
                    2
                ) AS percentageActive,
                ROUND(
                    (
                        COUNT(*) FILTER (WHERE active = false)::numeric
                        / NULLIF(COUNT(*), 0)
                    ) * 100,
                    2
                ) AS percentageInactive
            FROM tb_service_management
            """,
            nativeQuery = true)
    ResponseStatisticsDto getStatistics();
}
