package br.com.queue.dtos.attendance.statistics;

import java.math.BigDecimal;

public record ResponseAverageWaitingTimeStatisticsDto(
        BigDecimal averageWaitingTime
) {
}
