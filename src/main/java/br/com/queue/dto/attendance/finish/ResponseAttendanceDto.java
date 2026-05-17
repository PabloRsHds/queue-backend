package br.com.queue.dto.attendance.finish;

import java.time.LocalDateTime;

public record ResponseAttendanceDto(
        String ticketId,
        String ticketCode,
        String observation,
        String resolution,
        LocalDateTime startedAt,
        LocalDateTime finishedAt
) {
}
