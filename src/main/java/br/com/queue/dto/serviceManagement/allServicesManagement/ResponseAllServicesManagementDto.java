package br.com.queue.dto.serviceManagement.allServicesManagement;

public record ResponseAllServicesManagementDto(

        String serviceManagementId,
        String name,
        String code,
        String departmentName,
        Boolean active
) {
}
