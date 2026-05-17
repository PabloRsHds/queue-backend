package br.com.queue.dto.serviceManagement.create;

public record CreateServiceManagementDto(

        String name,
        String code,
        String description,
        String departmentName
) {
}
