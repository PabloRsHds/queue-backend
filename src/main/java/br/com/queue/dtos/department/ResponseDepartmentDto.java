package br.com.queue.dtos.department;

public record ResponseDepartmentDto(
        String departmentId,
        String name,
        String description,
        Boolean active
) {
}
