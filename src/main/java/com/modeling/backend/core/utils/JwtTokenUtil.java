package com.modeling.backend.core.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Data
public class JwtTokenUtil {
	private static final String CLAIM_KEY_USERNAME = "subject";
	private static final String CLAIM_KEY_CREATED = "created";
	public static final long JWT_TOKEN_VALIDITY = 1 * 60 * 60;  // 1 hour

	private static String secret = "ModelingToolsBackend";

	// retrieve username from jwt token
	public static String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	// retrieve expiration date from jwt token
	public static Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}
    // for retrieveing any information from token we will need the secret key
	private static Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

    // check if the token has expired
	private static Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	// generate token for user
	public static String generateToken(String username) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_KEY_USERNAME, username);
		claims.put(CLAIM_KEY_CREATED, new Date());
		return doGenerateToken(claims, username);
	}

	// while creating the token -
	// 1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
	// 2. Sign the JWT using the HS512 algorithm and secret key.
	// 3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	//    compaction of the JWT to a URL-safe string
	private static String doGenerateToken(Map<String, Object> claims, String subject) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	// validate token
	public static Boolean validateToken(String token, String username) {
		final String usernameFromToken = getUsernameFromToken(token);
		return (usernameFromToken.equals(username) && !isTokenExpired(token));
	}
}
