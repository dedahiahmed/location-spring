package mr.iscae.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mr.iscae.constants.Role;
import mr.iscae.dtos.AuthenticationRequest;
import mr.iscae.dtos.AuthenticationResponse;
import mr.iscae.dtos.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import mr.iscae.entities.User;
import mr.iscae.repositories.UserRepository;


import java.io.IOException;

@Service
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Autowired
    public AuthenticationService(UserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(RegisterRequest request) {
        if (repository.findUserByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        var user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        repository.save(user); // Save the user information to the database
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            var user = repository.findUserByUsername(request.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findUserByUsername(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
    public Boolean isCurrentUserAdmin(HttpServletRequest request) {
        try {
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new AuthenticationCredentialsNotFoundException("No authentication token provided");
            }

            final String jwt = authHeader.substring(7);
            final String username = jwtService.extractUsername(jwt);

            if (username == null) {
                throw new AuthenticationCredentialsNotFoundException("Invalid token");
            }

            User user = repository.findUserByUsername(username)
                    .orElseThrow(() -> {
                        return new UsernameNotFoundException("User not found");
                    });

            boolean isTokenValid = jwtService.isTokenValid(jwt, user);

            if (!isTokenValid) {
                throw new AuthenticationCredentialsNotFoundException("Invalid token");
            }

            return user.getRole() == Role.ADMIN;

        } catch (AuthenticationException e) {
            System.out.println("Authentication exception: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("Exception during admin status validation: " + e.getMessage());
            throw new AuthenticationCredentialsNotFoundException("Error validating admin status", e);
        }
    }

}

