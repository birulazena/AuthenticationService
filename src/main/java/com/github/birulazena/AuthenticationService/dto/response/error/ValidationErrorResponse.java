package com.github.birulazena.AuthenticationService.dto.response.error;

import java.util.Map;

public record ValidationErrorResponse(String message,
                                      Map<String, String> errors) {
}
