package br.com.queue.dto.attendance.allAttendances;

import java.time.LocalDateTime;

public record ResponseAllAttendances(
        String ticketId,
        String ticketCode,
        String observation,
        String resolution,
        LocalDateTime startedAt,
        LocalDateTime finishedAt
) {
}
