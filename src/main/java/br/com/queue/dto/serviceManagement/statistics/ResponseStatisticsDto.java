package br.com.queue.dto.serviceManagement.statistics;

import java.math.BigDecimal;

public record ResponseStatisticsDto(
        Long totalElements,
        Long totalElementsActive,
        Long totalElementsInactive,
        BigDecimal percentageActive,
        BigDecimal percentageInactive
) {
}
