package br.com.queue.dto.department.create;

public record ResponseDepartmentDto(
        String departmentId,
        String name,
        String description
) {
}
