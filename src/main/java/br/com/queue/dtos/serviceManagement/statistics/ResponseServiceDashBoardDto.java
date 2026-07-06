package br.com.queue.dtos.serviceManagement.statistics;

import java.util.List;

public record ResponseServiceDashBoardDto(
        ResponseCountTotalServicesStatisticsDto countTotalServicesStatistics,
        ResponseServicePercentagesStatisticsDto servicePercentagesStatistics,
        List<ResponseServicesCreatedByMonthStatisticsDto> countServicesCreatedByMonth
) {
}
