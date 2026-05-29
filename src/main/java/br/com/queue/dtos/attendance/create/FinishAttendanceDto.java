package br.com.queue.dtos.attendance.create;

public record FinishAttendanceDto(
        String attendanceId,
        String resolution,
        String observation
) {
}
