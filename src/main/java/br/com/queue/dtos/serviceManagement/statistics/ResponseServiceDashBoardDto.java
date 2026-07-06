package br.com.queue.dtos.serviceManagement.statistics;

public record ResponseServiceDashBoardDto(
        ResponseCountTotalServicesStatisticsDto countTotalServicesStatistics,
        ResponseServicePercentagesStatisticsDto servicePercentagesStatistics
) {
}
