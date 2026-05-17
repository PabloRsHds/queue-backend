package br.com.queue.dto.attendance.create;

public record CreateAttendanceDto(
        String ticketId,
        String observation,
        String resolution
) {
}
