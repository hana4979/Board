package com.himedia.sp_server.security.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

public class JWTUtil {

    // 토큰 발급에 필요한 30자리 이상의 키값 String
    private static String key = "1234567890123456789012345678901234567890";

    /* 토큰 제작 메소드 */
    public static String generateToken(Map<String, Object> claims, int i) {
        SecretKey key = null;
        try {
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        String jwtStr = Jwts.builder()
                .setHeader(Map.of("typ", "JWT"))
                .setClaims(claims)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(i).toInstant()))
                .signWith(key)
                .compact();
        return jwtStr;
    }

    /* 토큰 점검 메소드 */
    public static Map<String, Object> validateToken(String accessToken) throws CustomJWTException {
        Map<String, Object> claim = null;

        try {
            SecretKey key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
            claim = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken) // 파싱 및 검증, 실패 시 에러
                    .getBody();

        }catch(ExpiredJwtException expiredJwtException){ // 유효기간 지남
            throw new CustomJWTException("Expired");
        }catch(InvalidClaimException invalidClaimException){ // 유효하지 않음(?
            throw new CustomJWTException("Invalid");
        }catch(JwtException jwtException){
            throw new CustomJWTException("JWTError");
        }catch(Exception e){
            throw new CustomJWTException("Error");
        }
        return claim;
    }
}
