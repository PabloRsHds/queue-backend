package br.com.queue.dto.department.getDepartment;

import java.util.List;

public record ResponseGetDepartment(
        String departmentId,
        String name,
        String description,
        Boolean active,
        String createdAt,
        String updatedAt,
        List<String> services
) {
}
