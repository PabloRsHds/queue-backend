package br.com.queue.repositories.serviceManagement;

import br.com.queue.dtos.department.statistics.ResponseCountTotalDepartmentsStatisticsDto;
import br.com.queue.dtos.department.statistics.ResponseDepartmentPercentagesStatisticsDto;
import br.com.queue.dtos.department.statistics.ResponseDepartmentsCreatedByMonthStatisticsDto;
import br.com.queue.dtos.serviceManagement.ResponseServiceManagementDto;
import br.com.queue.dtos.serviceManagement.statistics.ResponseCountTotalServicesStatisticsDto;
import br.com.queue.dtos.serviceManagement.statistics.ResponseServicePercentagesStatisticsDto;
import br.com.queue.dtos.serviceManagement.statistics.ResponseServicesCreatedByMonthStatisticsDto;
import br.com.queue.dtos.statistics.ResponseStatisticsDto;
import br.com.queue.entities.serviceManagement.ServiceManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ServiceManagementRepository extends JpaRepository<ServiceManagement, String> {

    // Importação de extenção para acentuações no sql
    // CREATE EXTENSION IF NOT EXISTS unaccent;

    Optional<ServiceManagement> findByServiceManagementId(String serviceManagementId);

    Set<ServiceManagement> findAllByServiceManagementIdIn(
            Set<String> serviceManagementIds);

    @Modifying
    @Query(value = """
    DELETE FROM tb_user_services
    WHERE service_management_id = :id
    """, nativeQuery = true)
    void deleteUserServicesByServiceId(@Param("id") String id);

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
        ORDER BY COALESCE(s.updated_at, s.created_at) DESC
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

    // Statistics
    @Query(value = """
            SELECT
                COUNT(*) AS totalElements,
                COUNT(*) FILTER (WHERE active = true) AS totalElementsActive,
                COUNT(*) FILTER (WHERE active = false) AS totalElementsInactive
            FROM tb_service_management
            """,
            nativeQuery = true)
    ResponseCountTotalServicesStatisticsDto countTotalServicesStatisticsDto();

    @Query(value = """
            SELECT
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
            FROM tb_departments
            """,
            nativeQuery = true
    )
    ResponseServicePercentagesStatisticsDto getServicePercentagesStatisticsDto();

    @Query(value = """
            WITH months AS (
                SELECT generate_series(1, 12) AS month
            )
        
            SELECT
                m.month,
        
                CASE m.month
                    WHEN 1 THEN 'Jan'
                    WHEN 2 THEN 'Fev'
                    WHEN 3 THEN 'Mar'
                    WHEN 4 THEN 'Abr'
                    WHEN 5 THEN 'Mai'
                    WHEN 6 THEN 'Jun'
                    WHEN 7 THEN 'Jul'
                    WHEN 8 THEN 'Ago'
                    WHEN 9 THEN 'Set'
                    WHEN 10 THEN 'Out'
                    WHEN 11 THEN 'Nov'
                    WHEN 12 THEN 'Dez'
                END AS monthName,
        
                COALESCE(COUNT(s.service_management_id), 0) AS totalServices
        
            FROM months m
        
            LEFT JOIN tb_service_management s
                ON EXTRACT(MONTH FROM s.created_at) = m.month
                AND EXTRACT(YEAR FROM s.created_at) = EXTRACT(YEAR FROM CURRENT_DATE)
        
            GROUP BY
                m.month
        
            ORDER BY
                m.month
            """,
            nativeQuery = true)
    List<ResponseServicesCreatedByMonthStatisticsDto> countServicesCreatedByMonth();
}
