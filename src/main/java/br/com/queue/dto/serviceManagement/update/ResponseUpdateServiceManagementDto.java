package br.com.queue.dto.serviceManagement.update;

public record ResponseUpdateServiceManagementDto(

        String serviceManagementId,
        String name,
        String code,
        String description,
        String departmentId,
        String departmentName,
        Boolean active
) {
}
