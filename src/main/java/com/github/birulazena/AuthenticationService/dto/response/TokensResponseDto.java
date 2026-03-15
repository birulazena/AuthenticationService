package com.github.birulazena.AuthenticationService.dto.response;

public record TokensResponseDto(String accessToken,
                                String refreshToken,
                                String typeToken) {
}
