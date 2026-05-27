package br.com.queue.dto.serviceManagement.getServiceDto;

public record ResponseGetServiceByIdDto(

        String serviceManagementId,
        String name,
        String code,
        String description,
        String departmentId,
        String departmentName,
        Boolean active,
        String createdAt,
        String updatedAt
) {
}
