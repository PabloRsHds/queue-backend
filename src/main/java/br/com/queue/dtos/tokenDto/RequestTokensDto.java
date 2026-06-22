package br.com.queue.dtos.tokenDto;

import jakarta.validation.constraints.NotBlank;

public record RequestTokensDto(

        @NotBlank(message = "The accessToken cannot be blank")
        String accessToken,

        @NotBlank(message = "The refreshToken cannot be blank")
        String refreshToken
) {
}
