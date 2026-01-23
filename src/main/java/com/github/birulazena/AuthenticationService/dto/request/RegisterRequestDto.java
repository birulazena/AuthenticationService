package com.github.birulazena.AuthenticationService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDto(@NotBlank(message = "username must not be blank")
                                 String username,
                                 @NotBlank(message = "password must not be blank")
                                 String password,
                                 @NotNull(message = "User ID is required")
                                 Long userId) {
}
