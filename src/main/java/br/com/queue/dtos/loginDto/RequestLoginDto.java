package br.com.queue.dtos.loginDto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RequestLoginDto(

        String emailOrUsername,

        @Size(min = 8, max = 30, message = "The password must be between 8 and 30 characters")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$#!%&])\\S{8,}$",
                message = "Password must contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 symbol (no spaces)"
        )
        String password
) {
}
