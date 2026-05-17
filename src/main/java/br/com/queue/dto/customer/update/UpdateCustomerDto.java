package br.com.queue.dto.customer.update;

public record UpdateCustomerDto(
        String customerId,
        String name,
        String cpf,
        String rg,
        String phone,
        String email
) {
}
