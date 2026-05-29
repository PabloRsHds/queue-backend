package br.com.queue.dtos.ticket.startAttendance;

import java.time.LocalDateTime;

public record ResponseStartAttendanceDto(

        String ticketId,
        String code,
        String customerId,
        String customerName,
        String serviceManagementId,
        String serviceManagementName,
        String priority,
        String status,
        LocalDateTime createdAt,
        LocalDateTime calledAt,
        LocalDateTime startedAt,
        LocalDateTime finishedAt
) {
}
