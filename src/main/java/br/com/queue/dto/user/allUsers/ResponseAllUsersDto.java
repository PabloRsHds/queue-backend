package br.com.queue.dto.user.allUsers;

public record ResponseAllUsersDto(

        String userId,
        String username,
        String name,
        String surname,
        String email,
        String role,
        Integer counterNumber,
        Boolean active
) {
}
