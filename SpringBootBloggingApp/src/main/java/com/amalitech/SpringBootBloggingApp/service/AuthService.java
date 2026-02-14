package com.amalitech.SpringBootBloggingApp.service;

import com.amalitech.SpringBootBloggingApp.model.dto.request.LoginRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.request.RefreshTokenRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.request.RegisterRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.response.AuthResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.Role;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.amalitech.SpringBootBloggingApp.repository.RoleRepository;
import com.amalitech.SpringBootBloggingApp.repository.UserRepository;
import com.amalitech.SpringBootBloggingApp.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;
    private final SessionTrackingService sessionTrackingService;

    public AuthService(UserRepository userRepository,
                      RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder,
                      JwtUtil jwtUtil,
                      AuthenticationManager authenticationManager,
                      TokenBlacklistService tokenBlacklistService,
                      SessionTrackingService sessionTrackingService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.tokenBlacklistService = tokenBlacklistService;
        this.sessionTrackingService = sessionTrackingService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(System.currentTimeMillis());
        user.setUpdatedAt(System.currentTimeMillis());
        user.setEnabled(true);
        user.setAccountNonLocked(true);

        Role readerRole = roleRepository.findByName("READER")
                .orElseThrow(() -> new RuntimeException("Default READER role not found"));
        
        Set<Role> roles = new HashSet<>();
        roles.add(readerRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        logger.info("New user registered: {}", savedUser.getUsername());

        return authenticateAndGenerateTokens(request.getUsername(), request.getPassword(), null);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            // Find user by email first
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
            
            return authenticateAndGenerateTokens(user.getUsername(), request.getPassword(), request.getIpAddress());
        } catch (BadCredentialsException e) {
            logger.warn("Failed login attempt for email: {}", request.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        
        try {
            String username = jwtUtil.extractUsername(refreshToken);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (!jwtUtil.isTokenExpired(refreshToken)) {
                UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUsername())
                        .password(user.getPasswordHash())
                        .authorities(user.getRoles().stream()
                                .map(role -> "ROLE_" + role.getName())
                                .toArray(String[]::new))
                        .build();

                String newAccessToken = jwtUtil.generateToken(userDetails);
                String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

                List<String> roles = user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList());

                logger.info("Token refreshed for user: {}", username);

                return new AuthResponse(
                        newAccessToken,
                        newRefreshToken,
                        jwtUtil.getJwtExpirationMs(),
                        user.getUsername(),
                        user.getEmail(),
                        roles
                );
            } else {
                throw new IllegalArgumentException("Refresh token is expired");
            }
        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid refresh token");
        }
    }

    private AuthResponse authenticateAndGenerateTokens(String username, String password, String ipAddress) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // Track session
        if (ipAddress != null) {
            sessionTrackingService.createSession(accessToken, username, ipAddress);
        }

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toList());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        logger.info("User authenticated successfully: {}", username);

        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtUtil.getJwtExpirationMs(),
                user.getUsername(),
                user.getEmail(),
                roles
        );
    }

    public void logout(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            long expirationTime = jwtUtil.extractExpiration(token).getTime();
            
            tokenBlacklistService.blacklistToken(token, expirationTime);
            sessionTrackingService.invalidateSession(token);
            
            logger.info("User logged out: {}", username);
        } catch (Exception e) {
            logger.error("Error during logout: {}", e.getMessage());
            throw new RuntimeException("Logout failed");
        }
    }
}
