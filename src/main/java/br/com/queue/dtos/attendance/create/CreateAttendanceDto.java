package br.com.queue.dtos.attendance.create;

public record CreateAttendanceDto(
        String ticketId,
        String observation,
        String resolution
) {
}
