package br.com.queue.dto.schedule.create;

import java.time.LocalDateTime;

public record ResponseScheduleDto(

        String scheduleId,
        String customerId,
        String customerName,
        String serviceManagementId,
        String serviceManagementName,
        LocalDateTime scheduledDate,
        String status,
        String notes,
        LocalDateTime createdAt
) {
}
