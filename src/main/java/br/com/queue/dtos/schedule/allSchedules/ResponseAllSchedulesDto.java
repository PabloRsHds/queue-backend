package br.com.queue.dtos.schedule.allSchedules;

import java.time.LocalDateTime;

public record ResponseAllSchedulesDto(

        String scheduleId,
        String customerId,
        String customerName,
        String serviceManagementId,
        String serviceManagementName,
        LocalDateTime scheduleDate,
        String status
) {
}
