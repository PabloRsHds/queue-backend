package br.com.queue.dtos.user;

public record ResponseUserDto(

        String userId,
        String username,
        String name,
        String surname,
        String email,
        String role,
        Integer counterNumber,
        Boolean active,
        String createdAt,
        String updatedAt
) {
}
