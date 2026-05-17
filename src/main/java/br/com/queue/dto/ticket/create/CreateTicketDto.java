package br.com.queue.dto.ticket.create;

public record CreateTicketDto(
        String customerId,
        String serviceManagementId,
        String priority,
        String scheduleId
) {
}
