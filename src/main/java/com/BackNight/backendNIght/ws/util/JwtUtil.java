package com.BackNight.backendNIght.ws.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static String generateToken(String username, String correo, String nombre) {
        long expirationTimeMillis = 1000 * 60 * 60; // 1 hora


        return Jwts.builder()
                .setSubject(username)
                .claim("usuario", username)
                .claim("correo", correo)
                .claim("nombre", nombre)
                .setIssuer("BackNightAPI")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMillis))
                .signWith(key)
                .compact();
    }

}