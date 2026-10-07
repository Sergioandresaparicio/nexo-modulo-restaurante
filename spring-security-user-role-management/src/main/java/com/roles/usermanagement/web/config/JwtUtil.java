package com.roles.usermanagement.web.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.time.Instant;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/** Firma y verifica los tokens usando la configuración security.jwt. */
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class JwtUtil {
    private final JwtProperties properties;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtUtil(JwtProperties properties) {
        this.properties=properties;
        this.algorithm=Algorithm.HMAC256(properties.secret());
        this.verifier=JWT.require(algorithm).withIssuer(properties.issuer()).build();
    }

    public String create(String username) {
        Instant now=Instant.now();
        return JWT.create().withSubject(username).withIssuer(properties.issuer())
                .withIssuedAt(now).withExpiresAt(now.plus(properties.expiration())).sign(algorithm);
    }

    public boolean isValid(String token) {
        if(token==null || token.isBlank()) return false;
        try {
            verifier.verify(token);
            return true;
        } catch(JWTVerificationException exception) {
            return false;
        }
    }

    public String getUsername(String token) {
        return verifier.verify(token).getSubject();
    }
}
