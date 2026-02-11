package com.amalitech.SpringBootBloggingApp.security;

import com.amalitech.SpringBootBloggingApp.model.entity.Role;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.amalitech.SpringBootBloggingApp.repository.RoleRepository;
import com.amalitech.SpringBootBloggingApp.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;

    @Value("${app.oauth2.success-redirect-url}")
    private String successRedirectUrl;

    public OAuth2AuthenticationSuccessHandler(
            UserRepository userRepository,
            RoleRepository roleRepository,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        // Extract user info from OAuth2 provider
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String providerId = oauth2User.getAttribute("sub");
        String provider = "google"; // Can be extracted from registration ID

        // Find or create user
        User user = findOrCreateUser(email, name, provider, providerId);

        // Generate JWT tokens
        UserDetails userDetails = createUserDetails(user);
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // Redirect to frontend with tokens
        String targetUrl = UriComponentsBuilder.fromUriString(successRedirectUrl)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .queryParam("username", user.getUsername())
                .queryParam("email", user.getEmail())
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private User findOrCreateUser(String email, String name, String provider, String providerId) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Update OAuth2 info if changed
            user.setProvider(provider);
            user.setProviderId(providerId);
            user.setUpdatedAt(System.currentTimeMillis());
            return userRepository.save(user);
        }

        // Create new user
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(generateUsername(name, email));
        newUser.setPasswordHash(""); // No password for OAuth2 users
        newUser.setProvider(provider);
        newUser.setProviderId(providerId);
        newUser.setEnabled(true);
        newUser.setAccountNonLocked(true);
        newUser.setCreatedAt(System.currentTimeMillis());
        newUser.setUpdatedAt(System.currentTimeMillis());

        // Assign default READER role
        Role readerRole = roleRepository.findByName("READER")
                .orElseThrow(() -> new RuntimeException("READER role not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(readerRole);
        newUser.setRoles(roles);

        return userRepository.save(newUser);
    }

    private String generateUsername(String name, String email) {
        String baseUsername;
        if (name != null && !name.isEmpty()) {
            baseUsername = name.toLowerCase().replaceAll("\\s+", "_");
        } else {
            baseUsername = email.split("@")[0].toLowerCase();
        }

        // Ensure uniqueness
        String username = baseUsername;
        int counter = 1;
        while (userRepository.findByUsername(username).isPresent()) {
            username = baseUsername + "_" + counter++;
        }

        return username;
    }

    private UserDetails createUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                        .collect(Collectors.toSet()))
                .accountExpired(false)
                .accountLocked(!user.isAccountNonLocked())
                .credentialsExpired(false)
                .disabled(!user.isEnabled())
                .build();
    }
}
