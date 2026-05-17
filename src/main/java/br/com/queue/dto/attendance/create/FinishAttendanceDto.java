package br.com.queue.dto.attendance.create;

public record FinishAttendanceDto(
        String attendanceId,
        String resolution,
        String observation
) {
}
