package br.com.queue.dtos.user.update;

public record ResponseUpdateUserDto(

        String userId,
        String username,
        String surname,
        String email,
        String role,
        Integer counterNumber,
        Boolean active
) {
}
