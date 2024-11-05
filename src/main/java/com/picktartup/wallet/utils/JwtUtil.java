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

