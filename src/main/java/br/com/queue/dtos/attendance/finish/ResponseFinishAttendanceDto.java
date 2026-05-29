package br.com.queue.dtos.attendance.finish;

import java.time.LocalDateTime;

public record ResponseFinishAttendanceDto(
        String observation,
        String resolution,
        LocalDateTime finishedAt
) {
}
