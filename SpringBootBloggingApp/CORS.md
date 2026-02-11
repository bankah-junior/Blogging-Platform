# CORS vs CSRF: Complete Guide for Spring Boot Blogging Platform

## Table of Contents
1. [CORS Overview](#cors-overview)
2. [CSRF Overview](#csrf-overview)
3. [Key Differences](#key-differences)
4. [Implementation in This Project](#implementation-in-this-project)
5. [Testing Guide](#testing-guide)
6. [Security Best Practices](#security-best-practices)

---

## CORS Overview

### What is CORS?
**Cross-Origin Resource Sharing (CORS)** is a browser security mechanism that controls which external domains can make HTTP requests to your API.

### How CORS Works
- Browser enforces **Same-Origin Policy** by default
- CORS headers tell the browser which cross-origin requests are allowed
- Only affects **browser-based** requests (not Postman, cURL, or server-to-server)

### CORS Headers
```
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: GET, POST, PUT, DELETE
Access-Control-Allow-Headers: Content-Type, Authorization
Access-Control-Allow-Credentials: true
```

### When You Need CORS
- ✅ Frontend app (React, Angular, Vue) on different domain/port
- ✅ JavaScript fetch/axios requests from browsers
- ❌ Postman (no CORS enforcement)
- ❌ Mobile apps (no browser, no CORS)
- ❌ Server-to-server API calls

---

## CSRF Overview

### What is CSRF?
**Cross-Site Request Forgery (CSRF)** is an attack where malicious sites trick users into making unwanted requests to authenticated sites.

### How CSRF Attacks Work
1. User logs into `bank.com` (session cookie stored)
2. User visits malicious site `evil.com`
3. `evil.com` triggers hidden request to `bank.com/transfer`
4. Browser automatically sends session cookie
5. Bank processes unauthorized transfer

### CSRF Protection Mechanism
- Server generates unique CSRF token
- Token embedded in forms/requests
- Server validates token on state-changing operations
- Tokens are **NOT sent** automatically by browsers

### When You Need CSRF Protection
- ✅ Session-based authentication (cookies)
- ✅ Form submissions with user state
- ❌ Stateless JWT authentication (tokens in headers)
- ❌ APIs used by mobile/desktop apps

---

## Key Differences

| Aspect | CORS | CSRF |
|--------|------|------|
| **Purpose** | Controls cross-origin access | Prevents unauthorized requests from malicious sites |
| **Enforced By** | Browser | Server |
| **Scope** | All cross-origin HTTP requests | State-changing operations (POST, PUT, DELETE) |
| **Protection Against** | Unauthorized domain access | Forged requests using user's credentials |
| **Works With** | Any authentication method | Session/cookie-based auth |
| **Required For JWT APIs** | Yes (for browser clients) | No (stateless, no cookies) |
| **Affects Postman** | No | No |

### Visual Comparison

```
CORS Scenario:
[Browser on localhost:3000] --X--> [API on localhost:8080]
                             BLOCKED (without CORS)
[Browser on localhost:3000] --✓--> [API on localhost:8080]
                             ALLOWED (with CORS headers)

CSRF Scenario:
User authenticated at bank.com
[evil.com] --> [Browser] --cookie--> [bank.com/transfer]
                          BLOCKED (with CSRF token validation)
```

---

## Implementation in This Project

### CORS Configuration (SecurityConfig.java)

Our project implements **global CORS configuration** for stateless JWT authentication:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    // Allowed origins (frontend apps)
    configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",  // React
            "http://localhost:4200",  // Angular
            "http://localhost:8080"   // JavaFX/Testing
    ));
    
    // Allowed HTTP methods
    configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
    ));
    
    // Allowed headers (including Authorization for JWT)
    configuration.setAllowedHeaders(Arrays.asList("*"));
    
    // Exposed headers (visible to JavaScript)
    configuration.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type"
    ));
    
    // Allow credentials (cookies, authorization headers)
    configuration.setAllowCredentials(true);
    
    // Cache preflight requests for 1 hour
    configuration.setMaxAge(3600L);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

### CSRF Configuration (SecurityConfig.java)

**CSRF is DISABLED** for our stateless JWT API:

```java
http.csrf(AbstractHttpConfigurer::disable)
```

**Why CSRF is Disabled:**
1. **Stateless Authentication**: JWT tokens stored in localStorage/sessionStorage
2. **No Cookies**: No automatic credential transmission by browser
3. **Header-Based**: JWT sent in `Authorization` header (manual, not automatic)
4. **Not Vulnerable**: Malicious sites can't access localStorage or add custom headers

### When to Enable CSRF

If your application uses **session-based authentication** with cookies:

```java
http.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    .ignoringRequestMatchers("/auth/login", "/auth/register")
)
```

Frontend would need to:
1. Fetch CSRF token from `/csrf` endpoint
2. Include token in `X-CSRF-TOKEN` header for all mutations

---

## Testing Guide

### Testing CORS

#### Test 1: Postman (No CORS Enforcement)
```bash
# Postman bypasses CORS - this will always work
GET http://localhost:8080/api/posts
Authorization: Bearer <your-jwt-token>
```

#### Test 2: Browser Console (CORS Enforced)
```javascript
// Open browser console on http://localhost:3000
fetch('http://localhost:8080/api/posts', {
    method: 'GET',
    headers: {
        'Authorization': 'Bearer ' + yourJwtToken
    }
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('CORS Error:', error));
```

**Expected Results:**
- ✅ **Success**: If origin is in `allowedOrigins` list
- ❌ **CORS Error**: If origin is not allowed

#### Test 3: CORS Preflight Request
```bash
# Browser automatically sends OPTIONS request before POST/PUT/DELETE
curl -X OPTIONS http://localhost:8080/api/posts \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Authorization, Content-Type"
```

**Expected Headers in Response:**
```
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
Access-Control-Allow-Headers: Authorization, Content-Type
Access-Control-Allow-Credentials: true
```

### Testing CSRF (When Enabled)

#### Test 1: Request Without CSRF Token (Should Fail)
```javascript
fetch('http://localhost:8080/api/posts', {
    method: 'POST',
    credentials: 'include', // Send cookies
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({ title: 'Test Post' })
})
// Expected: 403 Forbidden - CSRF token missing
```

#### Test 2: Request With CSRF Token (Should Succeed)
```javascript
// First, get CSRF token
fetch('http://localhost:8080/csrf', { credentials: 'include' })
    .then(response => response.json())
    .then(data => {
        const csrfToken = data.token;
        
        // Then make request with token
        return fetch('http://localhost:8080/api/posts', {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify({ title: 'Test Post' })
        });
    })
    .then(response => response.json())
    .then(data => console.log('Success:', data));
```

---

## Security Best Practices

### CORS Security

#### ✅ DO
- **Specify exact origins** in production
- **Use environment variables** for origin configuration
- **Enable CORS only for trusted domains**
- **Validate JWT tokens** on the server
- **Use HTTPS** in production

#### ❌ DON'T
- Don't use `"*"` for `allowedOrigins` with credentials
- Don't allow all origins in production
- Don't expose sensitive headers unnecessarily
- Don't rely on CORS for authentication

### CSRF Security

#### ✅ DO (For Session-Based Auth)
- **Enable CSRF protection** for state-changing operations
- **Use HTTP-only cookies** for session tokens
- **Validate CSRF tokens** on server
- **Use SameSite cookie attribute**
- **Regenerate tokens** after authentication

#### ❌ DON'T
- Don't disable CSRF for session-based auth
- Don't store CSRF tokens in localStorage
- Don't skip CSRF validation for admin endpoints
- Don't use predictable token generation

### JWT-Specific Security

#### ✅ DO
- **Store JWT in httpOnly cookies** OR **localStorage** (trade-offs apply)
- **Use short expiration times** (15-30 minutes)
- **Implement token refresh** mechanism
- **Sign tokens** with strong secret
- **Validate tokens** on every request
- **Implement token blacklist** for logout

#### ❌ DON'T
- Don't store sensitive data in JWT payload
- Don't use weak signing algorithms
- Don't skip token expiration validation
- Don't trust client-side token validation

---

## Comparison Table: CORS vs CSRF

| Scenario | CORS Needed? | CSRF Needed? | Notes |
|----------|-------------|--------------|-------|
| React app (JWT) | ✅ Yes | ❌ No | CORS for cross-origin, no CSRF for stateless JWT |
| Mobile app (JWT) | ❌ No | ❌ No | No browser, no CORS enforcement |
| Session cookies | ✅ Yes | ✅ Yes | Both needed for browser security |
| Postman testing | ❌ No | ❌ No | Postman bypasses browser security |
| Server-to-server | ❌ No | ❌ No | No browser involved |
| GraphQL playground | ✅ Yes | ❌ No | Browser-based, needs CORS |

---

## Configuration Reference

### Current Project Configuration

**Authentication Method**: JWT (Stateless)  
**CORS**: ✅ Enabled (Global configuration)  
**CSRF**: ❌ Disabled (Not needed for JWT)  
**Session Management**: Stateless  
**Allowed Origins**: `localhost:3000`, `localhost:4200`, `localhost:8080`

### Environment-Specific Configuration

#### Development (application-dev.yml)
```yaml
cors:
  allowed-origins:
    - http://localhost:3000
    - http://localhost:4200
    - http://localhost:8080
```

#### Production (application-prod.yml)
```yaml
cors:
  allowed-origins:
    - https://yourdomain.com
    - https://www.yourdomain.com
```

---

## Common CORS Errors and Solutions

### Error 1: "No 'Access-Control-Allow-Origin' header"
**Cause**: Origin not in `allowedOrigins` list  
**Solution**: Add origin to CORS configuration

### Error 2: "CORS policy: credentials mode is 'include'"
**Cause**: `allowedOrigins = "*"` with `allowCredentials = true`  
**Solution**: Specify exact origins instead of wildcard

### Error 3: "Preflight request didn't succeed"
**Cause**: OPTIONS request not handled properly  
**Solution**: Ensure `OPTIONS` is in `allowedMethods`

---

## Testing Checklist

- [ ] Test CORS with browser fetch from different origin
- [ ] Verify Postman access (should work without CORS issues)
- [ ] Test preflight requests (OPTIONS)
- [ ] Verify JWT authentication works with CORS
- [ ] Test unauthorized origin is blocked
- [ ] Verify credentials (cookies/auth headers) are sent
- [ ] Test all HTTP methods (GET, POST, PUT, DELETE)
- [ ] Verify exposed headers are accessible to JavaScript
- [ ] Test with production origins (if applicable)
- [ ] Document any custom CORS requirements

---

**Last Updated**: 2026-02-11  
**Project**: Spring Boot Blogging Platform  
**Phase**: Spring Security Integration - Epic 1 Completed