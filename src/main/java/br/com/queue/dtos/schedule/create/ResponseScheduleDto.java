package br.com.queue.dtos.schedule.create;

import java.time.LocalDateTime;

public record ResponseScheduleDto(

        String scheduleId,
        String customerId,
        String customerName,
        String serviceManagementId,
        String serviceManagementName,
        String ticketId,
        LocalDateTime scheduledDate,
        String status,
        String note,
        String createdAt,
        String updateAt
) {
}
