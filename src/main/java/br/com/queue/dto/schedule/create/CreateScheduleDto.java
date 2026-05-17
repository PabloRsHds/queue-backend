package br.com.queue.dto.schedule.create;

public record CreateScheduleDto(
        String customerId,
        String serviceManagementId,
        String notes
) {
}
