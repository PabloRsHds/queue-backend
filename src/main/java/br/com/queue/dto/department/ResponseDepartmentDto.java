package br.com.queue.dto.department;

public record ResponseDepartmentDto(
        String departmentId,
        String name,
        String description
) {
}
