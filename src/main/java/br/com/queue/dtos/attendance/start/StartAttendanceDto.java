package br.com.queue.dtos.attendance.start;

public record StartAttendanceDto(
        String ticketId,
        String userId,
        String observation,
        String resolution
) {
}
