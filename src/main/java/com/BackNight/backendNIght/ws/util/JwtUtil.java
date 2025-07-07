package com.BackNight.backendNIght.ws.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys; // Necesitarás la dependencia jjwt-api y jjwt-impl
import io.jsonwebtoken.io.Decoders; // Necesitarás la dependencia jjwt-jackson o jjwt-gson

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JwtUtil {

    // ¡¡¡CAMBIA ESTO POR UNA CLAVE SECRETA FUERTE Y LARGA EN PRODUCCIÓN!!!
    // DEBE SER LA MISMA CLAVE SI ESTÁS COMPARTIMENTANDO LA LÓGICA DE CLIENTE/ADMIN
    // Una clave de ejemplo segura (genera una nueva en producción)
    private static final String SECRET_STRING = "thisisasecretkeyfornightplusapplicationthatsverylongandsecure";
    private static final Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_STRING));


    // Modificado para incluir el idUsuario (puede ser idAdmin o idCliente)
    public static String generateToken(String username, String email, String name, Integer idUsuario) {
        long expirationTimeMillis = 1000 * 60 * 60 * 10; // 10 horas de validez

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("name", name);
        claims.put("idUsuario", idUsuario); // Añadir el ID del usuario/admin a los claims

        return Jwts.builder()
                .setSubject(username)
                .setClaims(claims) // Usar setClaims para añadir todos los claims personalizados
                .setIssuer("BackNightAPI")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMillis))
                .signWith(key, SignatureAlgorithm.HS256) // Especificar el algoritmo aquí
                .compact();
    }

    // Método para extraer todos los claims del token
    private static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Método genérico para extraer un claim específico
    private static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extraer el nombre de usuario (subject) del token
    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extraer la fecha de expiración del token
    private static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Verificar si el token ha expirado
    public static Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validar el token contra un nombre de usuario
    public static Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // Nuevo método para extraer el idAdmin/idCliente del token (renombrado para ser genérico)
    public static Integer extractIdUsuarioFromToken(String token) {
        final Claims claims = extractAllClaims(token);
        // Asegúrate de que el claim 'idUsuario' sea de tipo Integer
        return claims.get("idUsuario", Integer.class);
    }
}
