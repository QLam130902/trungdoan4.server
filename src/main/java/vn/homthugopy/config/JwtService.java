package vn.homthugopy.config;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Service
public class JwtService {

	// Khóa bí mật để mã hóa token. (Nên cấu hình trong application.properties, nhưng để đơn giản ta gán cứng hoặc lấy mặc định)
	@Value("${jwt.secret:MySuperSecretKeyForTrungDoan4!@#}")
	private String secretKey;

	// Thời gian sống của token: 24 tiếng
	private static final long EXPIRATION_TIME = 86400000;

	// Tạo Token từ thông tin User
	public String generateToken(UserDetails userDetails) {
		return JWT.create()
				.withSubject(userDetails.getUsername())
				.withIssuedAt(new Date(System.currentTimeMillis()))
				.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.sign(Algorithm.HMAC256(secretKey));
	}

	// Lấy username từ Token
	public String extractUsername(String token) {
		try {
			return JWT.require(Algorithm.HMAC256(secretKey))
					.build()
					.verify(token)
					.getSubject();
		} catch (JWTVerificationException e) {
			return null;
		}
	}

	// Kiểm tra token có hợp lệ không
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	// Kiểm tra xem token đã hết hạn chưa
	private boolean isTokenExpired(String token) {
		try {
			Date expiration = JWT.require(Algorithm.HMAC256(secretKey))
					.build()
					.verify(token)
					.getExpiresAt();
			return expiration.before(new Date());
		} catch (JWTVerificationException e) {
			return true;
		}
	}
}
