package com.hospital.appointment_system;

import com.hospital.appointment_system.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    // TEST 1 — generate token not null
    @Test
    void generateToken_shouldReturnToken_whenEmailProvided() {
        String token = jwtUtil.generateToken("test@gmail.com");
        assertNotNull(token);
    }

    // TEST 2 — extract email from token
    @Test
    void extractEmail_shouldReturnEmail_whenTokenValid() {
        String token = jwtUtil.generateToken("test@gmail.com");
        String email = jwtUtil.extractEmail(token);
        assertEquals("test@gmail.com", email);
    }

    // TEST 3 — token is valid
    @Test
    void isTokenValid_shouldReturnTrue_whenTokenValid() {
        String token = jwtUtil.generateToken("test@gmail.com");
        assertTrue(jwtUtil.isTokenValid(token));
    }

    // TEST 4 — token is not expired
    @Test
    void isTokenExpired_shouldReturnFalse_whenTokenFresh() {
        String token = jwtUtil.generateToken("test@gmail.com");
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    // TEST 5 — invalid token
    @Test
    void isTokenValid_shouldReturnFalse_whenTokenInvalid() {
        assertFalse(jwtUtil.isTokenValid("invalid.token.here"));
    }

    // TEST 6 — different emails generate different tokens
    @Test
    void generateToken_shouldGenerateDifferentTokens_forDifferentEmails() {
        String token1 = jwtUtil.generateToken("user1@gmail.com");
        String token2 = jwtUtil.generateToken("user2@gmail.com");
        assertNotEquals(token1, token2);
    }
}