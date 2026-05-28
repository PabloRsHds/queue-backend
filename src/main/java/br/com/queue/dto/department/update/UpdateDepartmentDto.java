package br.com.queue.dto.department.update;

public record UpdateDepartmentDto(
        String departmentId,
        String name,
        String description,
        Boolean active
) {
}
