package br.com.queue.dto.customer.allCustomer;

import java.time.LocalDateTime;

public record ResponseAllCustomersDto(
        String name,
        String cpf,
        String rg,
        String phone,
        String email,
        LocalDateTime createdAt
) {
}
