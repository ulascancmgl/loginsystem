package com.basiclogin.loginsystem.Entity;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;

public class JwtTokenUtil {

    private static final int SECRET_KEY_SIZE_BYTES = 32; // 256 bits
    private static final String secretKey = generateRandomSecretKey();
    private static final long expirationMs = 86400000; // 1 day in milliseconds

    private static String generateRandomSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] secretKeyBytes = new byte[SECRET_KEY_SIZE_BYTES];
        random.nextBytes(secretKeyBytes);
        return DatatypeConverter.printBase64Binary(secretKeyBytes);
    }

    public static String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS512.getJcaName());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey)
                .compact();
    }
}
