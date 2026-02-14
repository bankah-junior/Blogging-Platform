package com.amalitech.SpringBootBloggingApp.security;

import com.amalitech.SpringBootBloggingApp.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService, 
                                   TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/oauth2/") || 
               path.startsWith("/login/oauth2/") ||
               path.equals("/login") ||
               path.equals("/error");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        logger.info("=== JWT Filter Processing: {} ===", path);
        
        try {
            String jwt = extractJwtFromRequest(request);
            logger.info("JWT Token extracted: {}", jwt != null ? "YES (length: " + jwt.length() + ")" : "NO");

            if (StringUtils.hasText(jwt)) {
                // Check blacklist
                if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
                    logger.warn("Token is BLACKLISTED for path: {}", path);
                    filterChain.doFilter(request, response);
                    return;
                }
                logger.info("Token is NOT blacklisted");

                // Extract username
                String username = null;
                try {
                    username = jwtUtil.extractUsername(jwt);
                    logger.info("Username extracted from token: {}", username);
                } catch (Exception e) {
                    logger.error("Failed to extract username from token: {}", e.getMessage(), e);
                    filterChain.doFilter(request, response);
                    return;
                }

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    logger.info("Loading user details for: {}", username);
                    
                    UserDetails userDetails = null;
                    try {
                        userDetails = userDetailsService.loadUserByUsername(username);
                        logger.info("User details loaded. Authorities: {}", userDetails.getAuthorities());
                    } catch (Exception e) {
                        logger.error("Failed to load user details: {}", e.getMessage(), e);
                        filterChain.doFilter(request, response);
                        return;
                    }

                    // Validate token
                    try {
                        if (jwtUtil.validateToken(jwt, userDetails)) {
                            logger.info("Token is VALID");
                            
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities()
                                    );
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            
                            logger.info("✓ Authentication SET for user: {} with authorities: {}", 
                                    username, userDetails.getAuthorities());
                        } else {
                            logger.warn("Token validation FAILED for user: {}", username);
                        }
                    } catch (Exception e) {
                        logger.error("Token validation threw exception: {}", e.getMessage(), e);
                    }
                } else {
                    if (username == null) {
                        logger.warn("Username is NULL after extraction");
                    }
                    if (SecurityContextHolder.getContext().getAuthentication() != null) {
                        logger.info("Authentication already set in SecurityContext");
                    }
                }
            } else {
                logger.warn("No JWT token found in Authorization header for path: {}", path);
            }
        } catch (Exception ex) {
            logger.error("EXCEPTION in JWT filter for path: {} - {}", path, ex.getMessage(), ex);
        }

        logger.info("=== Filter Chain Continue ===");
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        logger.info("Authorization header: {}", bearerToken != null ? "Present" : "NULL");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            logger.info("Extracted Bearer token (first 20 chars): {}", token.substring(0, Math.min(20, token.length())));
            return token;
        }
        
        if (bearerToken != null && !bearerToken.startsWith("Bearer ")) {
            logger.warn("Authorization header present but doesn't start with 'Bearer ': {}", bearerToken.substring(0, Math.min(30, bearerToken.length())));
        }
        
        return null;
    }
}
