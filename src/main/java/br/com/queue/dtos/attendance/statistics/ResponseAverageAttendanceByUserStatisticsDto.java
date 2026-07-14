package br.com.queue.dtos.attendance.statistics;

import java.math.BigDecimal;

public record ResponseAverageAttendanceByUserStatisticsDto(
        String username,
        BigDecimal averageMinutes
) {
}
