package com.BackNight.backendNIght.ws.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys; // Importar Keys

import java.security.Key; // Importar Key
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JwtUtil {

    // Genera una clave segura para la firma. ¡IMPORTANTE: Usa una clave fuerte y mantenla secreta!
    // En un entorno de producción, esta clave debería ser una variable de entorno.
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Genera una clave segura

    // Tiempo de validez del token (ej. 10 horas en milisegundos)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 horas

    /**
     * Genera un token JWT para un cliente.
     * @param usuario El nombre de usuario del cliente.
     * @param correo El correo del cliente.
     * @param nombre El nombre completo del cliente.
     * @param idCliente El ID numérico del cliente.
     * @return El token JWT generado.
     */
    public static String generateToken(String usuario, String correo, String nombre, Integer idCliente) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("usuario", usuario);
        claims.put("correo", correo);
        claims.put("nombre", nombre);
        claims.put("id_cliente", idCliente); // <--- ¡EL ID DEL CLIENTE SE PONE AQUÍ!
        return createToken(claims, usuario); // El 'subject' del token es el usuario
    }

    private static String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // El sujeto suele ser el identificador principal (ej. username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrae todos los claims (cuerpo) del token JWT.
     * @param token El token JWT.
     * @return Los claims del token.
     */
    public static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder() // Usar parserBuilder para la nueva API de JJWT
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae el nombre de usuario (subject) del token.
     * @param token El token JWT.
     * @return El nombre de usuario.
     */
    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae el ID del cliente del token.
     * @param token El token JWT.
     * @return El ID numérico del cliente.
     */
    public static Integer extractIdUsuarioFromToken(String token) {
        // Asegúrate de que el claim "id_cliente" se extrae como Integer
        return extractClaim(token, claims -> claims.get("id_cliente", Integer.class));
    }

    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public static Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public static Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
