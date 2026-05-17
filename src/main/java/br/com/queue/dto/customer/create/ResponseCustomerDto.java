package br.com.queue.dto.customer.create;

import java.time.LocalDateTime;

public record ResponseCustomerDto(
        String name,
        String cpf,
        String rg,
        String phone,
        String email,
        LocalDateTime createdAt
) {
}
