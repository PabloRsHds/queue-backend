package br.com.queue.dto.serviceManagement;

public record ResponseServiceManagementDto(

        String serviceManagementId,
        String name,
        String code,
        String description,
        String departmentId,
        String departmentName,
        Boolean active
) {
}
