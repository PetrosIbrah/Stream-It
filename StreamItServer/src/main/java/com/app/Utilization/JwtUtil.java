package com.app.Utilization;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;

public class JwtUtil {
    // Not so secrect for now. :)
    private static final String SECRET_KEY = "When-you-play-the-Game-Of-Thrones,-you-win-or-you-die,-there-is-no-middle-ground";

    public static String generateToken(String username, String email, String password) {
        return Jwts.builder()
                .setSubject(username)
                .claim("email", email)
                .claim("password", password)
                .setIssuedAt(new Date())
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public static boolean verifyPassword(String token, String password) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("password").equals(password);
    }
}
