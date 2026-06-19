package br.com.queue.dtos.schedule.statistics;

public record ResponseScheduleStatisticsDto(
        long schedulingOfDay,
        long customerPresent,
        long schedulingCanceled,
        long absentCustomer
) {
}
