package com.messenger_server.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtUtil {
	private String SECRET_KEY = "secret";

	public String extractUsername(String token) {
		return getDecodedJWT(token).getSubject();
	}

	private Date extractExpiration(String token) {
		return getDecodedJWT(token).getExpiresAt();
	}

	private DecodedJWT getDecodedJWT(String token) {
		Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
		JWTVerifier verifier = JWT.require(algorithm).build();
		return verifier.verify(token);
	}

	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public String generateToken(UserDetails userDetails) {
		return JWT.create()
						.withSubject(userDetails.getUsername())
						.withIssuedAt(new Date(System.currentTimeMillis()))
						.withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
						.sign(Algorithm.HMAC256(SECRET_KEY));
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}
}
