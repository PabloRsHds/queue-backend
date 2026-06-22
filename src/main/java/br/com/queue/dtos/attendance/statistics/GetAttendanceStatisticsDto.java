package br.com.queue.dtos.attendance.statistics;

import java.math.BigDecimal;

public record GetAttendanceStatisticsDto(

        long countAttendancesWaiting,
        long countAttendancesInProgress,
        long countAttendancesOfDay,
        BigDecimal averageWaitingTime,
        BigDecimal  averageServiceTime
) {
}
