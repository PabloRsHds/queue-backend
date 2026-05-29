package br.com.queue.dtos.schedule.create;

public record CreateScheduleDto(
        String customerId,
        String serviceManagementId,
        String notes
) {
}
