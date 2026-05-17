package br.com.queue.dto.customer.create;

public record CreateCustomerDto(
        String name,
        String cpf,
        String rg,
        String phone,
        String email
) {
}
