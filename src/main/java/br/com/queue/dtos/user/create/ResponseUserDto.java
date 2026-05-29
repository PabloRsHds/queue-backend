package br.com.queue.dtos.user.create;

public record ResponseUserDto(

        String userId,
        String username,
        String surname,
        String email,
        String role,
        Integer counterNumber,
        Boolean active
) {
}
