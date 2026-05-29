package br.com.queue.dtos.schedule.update;

import java.time.LocalDateTime;

public record ResponseUpdateScheduleDto(
        String scheduleId,
        String customerId,
        String customerName,
        String serviceManagementId,
        String serviceManagementName,
        LocalDateTime scheduledDate,
        String  status,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
