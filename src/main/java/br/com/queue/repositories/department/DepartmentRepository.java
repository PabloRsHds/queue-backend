package br.com.queue.repositories.department;

import br.com.queue.dto.department.ResponseDepartmentDto;
import br.com.queue.entities.department.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, String> {

    Optional<Department> findByName(String name);
    Optional<Department> findByDepartmentId(String departmentId);

    @Query(value = """
    SELECT
        d.department_id AS departmentId ,
        d.name AS name,
        d.description AS description,
        d.active AS active
    FROM tb_departments d
    WHERE (
        :search IS NULL
        OR :search = ''
        OR UNACCENT(LOWER(d.name)) LIKE UNACCENT(LOWER(CONCAT('%', :search, '%')))
    )
    ORDER BY COALESCE(d.updated_at, d.created_at) DESC;
    """,
        countQuery = """
    SELECT COUNT(*)
    FROM tb_departments d
    WHERE (
        :search IS NULL
        OR :search = ''
        OR UNACCENT(LOWER(d.name)) LIKE UNACCENT(LOWER(CONCAT('%', :search, '%')))
    )
    """,
        nativeQuery = true
    )
    Page<ResponseDepartmentDto> findAllWithSearch(@Param("search") String search, Pageable pageable);
}
