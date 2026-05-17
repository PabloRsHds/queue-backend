package br.com.queue.repositories.department;

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

    @Query("""
    SELECT d
    FROM Department d
    WHERE (:search IS NULL OR :search = '' OR LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    Page<Department> findAllWithSearch(@Param("search") String search, Pageable pageable);
}
