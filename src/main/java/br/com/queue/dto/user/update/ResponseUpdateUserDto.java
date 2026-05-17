package br.com.queue.dto.user.update;

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
