package br.com.queue.dto.department.allDepartment;

public record ResponseAllDepartmentDto(
        String departmentId,
        String name,
        String description
) {
}
