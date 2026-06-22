package br.com.queue.dtos.attendance.start;

public record FinishAttendanceDto(
        String attendanceId,
        String ticketId,
        String resolution,
        String observation
) {
}
