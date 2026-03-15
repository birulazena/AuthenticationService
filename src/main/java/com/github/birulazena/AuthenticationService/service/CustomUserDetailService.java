package com.github.birulazena.AuthenticationService.service;

import com.github.birulazena.AuthenticationService.entity.CustomUserDetails;
import com.github.birulazena.AuthenticationService.entity.User;
import com.github.birulazena.AuthenticationService.exception.UserNotFoundException;
import com.github.birulazena.AuthenticationService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with name " + username + "not found"));
        return new CustomUserDetails(user);
    }
}
