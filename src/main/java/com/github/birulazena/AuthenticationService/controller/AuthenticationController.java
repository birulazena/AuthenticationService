package com.github.birulazena.AuthenticationService.controller;

import com.github.birulazena.AuthenticationService.dto.request.LoginRequestDto;
import com.github.birulazena.AuthenticationService.dto.request.RegisterRequestDto;
import com.github.birulazena.AuthenticationService.dto.response.AccessTokenResponseDto;
import com.github.birulazena.AuthenticationService.dto.response.TokensResponseDto;
import com.github.birulazena.AuthenticationService.dto.response.ValidTokenResponseDto;
import com.github.birulazena.AuthenticationService.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/public/login")
    public ResponseEntity<TokensResponseDto> createToken(@RequestBody
                                                         LoginRequestDto loginRequestDto) {
        TokensResponseDto tokensResponseDto = authenticationService
                .createTokensLogin(loginRequestDto);
        return ResponseEntity.ok(tokensResponseDto);
    }

    @GetMapping("/validate")
    public ResponseEntity<ValidTokenResponseDto> validation(@RequestHeader("Authorization")
                                                            String token) {
        ValidTokenResponseDto validTokenResponseDto = authenticationService
                .validateToken(token);
        return ResponseEntity.ok(validTokenResponseDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponseDto> refresh(@RequestHeader("Authorization")
                                                          String token) {
        AccessTokenResponseDto accessTokenResponseDto = authenticationService
                .refreshToken(token);
        return ResponseEntity.ok(accessTokenResponseDto);
    }

    @PostMapping("/public/register")
    public ResponseEntity<Void> saveUser(@RequestBody
                                         RegisterRequestDto registerRequestDto) {
        authenticationService.saveUser(registerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserByUserId(@RequestHeader("Authorization")
                                             String token,
                                         @PathVariable Long userId)
            throws AccessDeniedException {

        authenticationService.deleteUserByUserId(token, userId);
        return ResponseEntity.noContent().build();
    }
}
