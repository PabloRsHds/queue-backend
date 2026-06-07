package br.com.queue.dtos.customer.allCustomer;

public record ResponseAllCustomersDto(
        String name,
        String cpf,
        String rg,
        String phone,
        String email
) {
}
