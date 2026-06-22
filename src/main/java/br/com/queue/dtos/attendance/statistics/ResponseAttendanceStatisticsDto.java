package br.com.queue.dtos.attendance.statistics;

public record ResponseAttendanceStatisticsDto(

        long countAttendancesWaiting,
        long countAttendancesInProgress,
        long countAttendancesOfDay,
        String averageWaitingTime,
        String averageServiceTime
) {
}
