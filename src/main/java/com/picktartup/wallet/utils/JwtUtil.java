<<<<<<< Updated upstream
package com.picktartup.wallet.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

  @Value("${jwt.secret}") // application.properties에서 jwt.secret 값을 로드
  private String secret;

  /**
   * JWT에서 모든 Claims(내용)를 추출
   */
  public Claims extractAllClaims(String token) {
    return Jwts.parser()
        .setSigningKey(secret)
        .parseClaimsJws(token)
        .getBody();
  }

  /**
   * JWT에서 사용자 ID 추출
   */
  public String extractUserId(String token) {
    return extractAllClaims(token).getSubject(); // 사용자 ID를 subject에 저장했다고 가정
  }

  /**
   * JWT가 만료되었는지 확인
   */
  public boolean isTokenExpired(String token) {
    Date expiration = extractAllClaims(token).getExpiration();
    return expiration.before(new Date());
  }

  /**
   * JWT 유효성 검증
   */
  public boolean validateToken(String token) {
    try {
      extractAllClaims(token);
      return !isTokenExpired(token);
    } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException |
             SignatureException | IllegalArgumentException e) {
      System.out.println("Invalid JWT: " + e.getMessage());
    }
    return false;
  }
}

=======
//package com.picktartup.wallet.utils;
//
//import static javax.crypto.Cipher.SECRET_KEY;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.SignatureException;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.MalformedJwtException;
//import io.jsonwebtoken.UnsupportedJwtException;
//import java.util.HashMap;
//import java.util.Map;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//
//@Component
//public class JwtUtil {
//
//  @Value("${jwt.secret}") // application.properties에서 jwt.secret 값을 로드
//  private String secret;
//
//  private final long EXPIRATION_TIME = 3600000;
//
//  public String generateToken(Users user) {
//    Map<String, Object> claims = new HashMap<>();
//    claims.put("userId", user.getUserId());
//    claims.put("email", user.getEmail());
//    claims.put("role", user.getRole().toString());
//    claims.put("walletId", user.getWallet().getWalletId());
//    claims.put("walletAddress", user.getWallet().getAddress());
//
//    return Jwts.builder()
//        .setClaims(claims)
//        .setSubject(user.getUsername())
//        .setIssuedAt(new Date(System.currentTimeMillis()))
//        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//        .signWith(SignatureAlgorithm.HS256, String.valueOf(SECRET_KEY))
//        .compact();
//  }
//
//
//  /**
//   * JWT에서 모든 Claims(내용)를 추출
//   */
//  public Claims extractAllClaims(String token) {
//    return Jwts.parser()
//        .setSigningKey(secret)
//        .parseClaimsJws(token)
//        .getBody();
//  }
//
//  /**
//   * JWT에서 사용자 ID 추출
//   */
//  public String extractUserId(String token) {
//    return extractAllClaims(token).getSubject(); // 사용자 ID를 subject에 저장했다고 가정
//  }
//
//  /**
//   * JWT가 만료되었는지 확인
//   */
//  public boolean isTokenExpired(String token) {
//    Date expiration = extractAllClaims(token).getExpiration();
//    return expiration.before(new Date());
//  }
//
//  /**
//   * JWT 유효성 검증
//   */
//  public boolean validateToken(String token) {
//    try {
//      extractAllClaims(token);
//      return !isTokenExpired(token);
//    } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException |
//             SignatureException | IllegalArgumentException e) {
//      System.out.println("Invalid JWT: " + e.getMessage());
//    }
//    return false;
//  }
//}
//
>>>>>>> Stashed changes
