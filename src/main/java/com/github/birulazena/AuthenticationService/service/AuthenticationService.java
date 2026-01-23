package com.github.birulazena.AuthenticationService.service;

import com.github.birulazena.AuthenticationService.dto.request.LoginRequestDto;
import com.github.birulazena.AuthenticationService.dto.request.RegisterRequestDto;
import com.github.birulazena.AuthenticationService.dto.response.AccessTokenResponseDto;
import com.github.birulazena.AuthenticationService.dto.response.TokensResponseDto;
import com.github.birulazena.AuthenticationService.dto.response.ValidTokenResponseDto;
import com.github.birulazena.AuthenticationService.entity.Role;
import com.github.birulazena.AuthenticationService.entity.User;
import com.github.birulazena.AuthenticationService.exception.InvalidPasswordException;
import com.github.birulazena.AuthenticationService.exception.InvalidRefreshTokenException;
import com.github.birulazena.AuthenticationService.exception.UserAlreadyExistsException;
import com.github.birulazena.AuthenticationService.exception.UserNotFoundException;
import com.github.birulazena.AuthenticationService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    public TokensResponseDto createTokensLogin(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByUsername(loginRequestDto.username())
                .orElseThrow(() -> new UserNotFoundException("User with name "
                        + loginRequestDto.username() + " not found"));

        if(!passwordEncoder.matches(loginRequestDto.password(), user.getPassword()))
            throw new InvalidPasswordException("wrong password");

        String accessToken = jwtService.generateAccessToken(
                user.getUsername(), user.getUserId(), user.getRole());
        String refreshToken = jwtService.generateRefreshToken(
                user.getUsername(), user.getUserId());

        return new TokensResponseDto(accessToken, refreshToken, "Bearer");
    }

    public ValidTokenResponseDto validateToken(String token) {
        String extractedToken = jwtService.extractToken(token);
        return new ValidTokenResponseDto(jwtService.validateToken(extractedToken));
    }

    public AccessTokenResponseDto refreshToken(String token) {
        String extractedToken = jwtService.extractToken(token);

        if(jwtService.isRefreshToken(extractedToken)){
            String username = jwtService.getUserFromToken(extractedToken);
            Long userId = jwtService.getUserIdFromToken(extractedToken);
            Role role = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User with name "
                            + username + " not found"))
                    .getRole();

            return new AccessTokenResponseDto(
                    jwtService.generateAccessToken(username, userId, role),
                    "Bearer");
        }

        throw new InvalidRefreshTokenException("This isn't a refresh token");
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void saveUser(RegisterRequestDto registerRequestDto) {
        if(userRepository.existsByUsername(registerRequestDto.username()))
            throw new UserAlreadyExistsException("User with username " +
                    registerRequestDto.username() + " already exists");

        userRepository.save(new User(
                null,
                registerRequestDto.username(),
                passwordEncoder.encode(registerRequestDto.password()),
                Role.USER,
                registerRequestDto.userId()
        ));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteUserByUserId(String token, Long id) throws AccessDeniedException {
        String extractedToken = jwtService.extractToken(token);
        if(jwtService.getRole(extractedToken).equals("ADMIN") ||
                jwtService.getUserIdFromToken(extractedToken).equals(id)) {

            userRepository.deleteUserByUserId(id);
            return;
        }
        throw new AccessDeniedException("insufficient rights to perform the operation");
    }


}
