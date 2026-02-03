```Java
// Option 1: Global CORS Configuration (Recommended for Spring Boot)
// Create this file: src/main/java/com/amalitech/bloggingplatform/config/CorsConfig.java

package com.amalitech.bloggingplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);

        // Allow requests from your React app
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:3000",
                "http://127.0.0.1:5173"
        ));

        // Allow all headers
        config.setAllowedHeaders(Arrays.asList(
                "Origin",
                "Content-Type",
                "Accept",
                "Authorization",
                "X-Requested-With",
                "X-Search-Keyword",
                "X-Sort-By",
                "X-Sort-Ascending",
                "X-Request-Time"
        ));

        // Allow all HTTP methods
        config.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "PATCH",
                "OPTIONS"
        ));

        // How long the response from a pre-flight request can be cached
        config.setMaxAge(3600L);

        // Expose headers to the client
        config.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}

// ============================================================================
// Option 2: WebMvcConfigurer Approach (Alternative for Spring Boot)
// ============================================================================

package com.amalitech.bloggingplatform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:5173",
                        "http://localhost:3000",
                        "http://127.0.0.1:5173"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}

// ============================================================================
// Option 3: Controller-Level CORS (For specific controllers only)
// ============================================================================

package com.amalitech.bloggingplatform.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(
        origins = {"http://localhost:5173", "http://localhost:3000"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowedHeaders = "*",
        allowCredentials = "true"
)
public class UserController {
    // Your existing controller methods...
}

// ============================================================================
// Option 4: Method-Level CORS (For specific endpoints only)
// ============================================================================

@RestController
@RequestMapping("/api/users")
public class UserController {

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/{userId}")
    public User getUserById(@PathVariable String userId) {
        // Your implementation
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/login")
    public User login(@RequestBody LoginRequest request) {
        // Your implementation
    }
}

// ============================================================================
// PRODUCTION CONFIGURATION
// ============================================================================

// For production, use environment variables
// application.properties or application.yml

// application.properties:
// cors.allowed.origins=https://yourdomain.com,https://www.yourdomain.com

// application.yml:
// cors:
//   allowed:
//     origins: https://yourdomain.com,https://www.yourdomain.com

@Configuration
public class ProductionCorsConfig {

    @Value("${cors.allowed.origins}")
    private String[] allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(allowedOrigins));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}

// ============================================================================
// SECURITY CONSIDERATIONS
// ============================================================================

/*
 * IMPORTANT SECURITY NOTES:
 *
 * 1. NEVER use "*" for allowedOrigins in production with credentials
 * 2. Always specify exact origins for production
 * 3. Use environment variables for origin configuration
 * 4. Limit exposed headers to only what's necessary
 * 5. Consider using HTTPS in production
 *
 * BAD (Don't use in production):
 * config.setAllowedOrigins(Arrays.asList("*"));
 *
 * GOOD (Use specific origins):
 * config.setAllowedOrigins(Arrays.asList(
 *     "https://yourdomain.com",
 *     "https://www.yourdomain.com"
 * ));
 */
```