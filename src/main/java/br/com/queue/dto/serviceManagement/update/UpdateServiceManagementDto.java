package br.com.queue.dto.serviceManagement.update;

public record UpdateServiceManagementDto(

        String serviceManagementId,
        String name,
        String code,
        String description,
        Boolean active,
        String departmentName
) {
}
