package ch.vindthing.security.jwt;

import java.util.Date;

import ch.vindthing.model.Store;
import ch.vindthing.model.User;
import ch.vindthing.repository.UserRepository;
import ch.vindthing.service.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${vindthing.app.jwtSecret}")
	private String jwtSecret;

	@Value("${vindthing.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	@Autowired
	UserRepository userRepository;

	/**
	 * Generate a new JWT Token
	 * @param authentication Auth object
	 * @return JWT Token
	 */
	public String generateJwtToken(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		return Jwts.builder()
				.setSubject((userPrincipal.getUsername()))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}

	/**
	 * Extracts the email from the jwt token
	 * @param token jwt token
	 * @return email
	 */
	public String getEmailFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	/**
	 * Gets the actual user object from the request jwt token
	 * @param token jwt token
	 * @return User or null if not found
	 */
	public User getUserFromJwtToken(String token){
		if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
			token = token.substring(7);
		}else{
			return null;
		}
		String email = getEmailFromJwtToken(token);
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + email));
	}

	/**
	 * Checks if the current User is the owner of the Store
	 * @param token JWT token
	 * @param store Store
	 * @return True if permitted
	 */
	public boolean checkPermissionOwner(String token, Store store){
		User user = getUserFromJwtToken(token);
		return store.getOwner().getId().equals(user.getId());
	}

	/**
	 * Checks if the current User is permitted for the Store
	 * @param token JWT token
	 * @param store Store
	 * @return True if permitted
	 */
	public boolean checkPermissionSharedUsers(String token, Store store){
		User user = getUserFromJwtToken(token);
		return store.getSharedUsers().stream().anyMatch(users -> user.getId().equals(users.getId()));
	}

	/**
	 * Validates JWT Tokens
	 * @param authToken jwt token
	 * @return true or false
	 */
	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
	}
}
