package config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component // đánh dấu một class là “bean” để Spring
//Bean: Một object (đối tượng) được Spring tạo ra, quản lý và sử dụng
public class JwtUtil {
    @Value("${jwt.secret}") // Lấy giá trị từ application.properties (jwt.secret) và gán vào biến secret
    private String secret;

    @Value("${jwt.expiration}")
    private Long  expiration;

//    lấy key để ký token
    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

//    create token
    public String generateToken(String email, String role){
        return Jwts.builder()
                .setSubject(email) // set email vào subject của token
                .claim("role", role) // thêm thông tin role vào token
                .setIssuedAt(new Date()) // thời gian tạo token
                .setExpiration(new Date((System.currentTimeMillis() + expiration))) // Thời gian hết hạn
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // ký token với thuật toán HS256 và key
                .compact(); // tạo token dưới dạng chuỗi
    }
// GIẢI MÃ TOKEN
private Claims extractClaims(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
}
//    Lấy email từ token
    public String extractEmail(String token){
        return extractClaims(token).getSubject();
    }

//    Lấy role từ token
    public  String extractRole(String token){
        return extractClaims(token).get("role", String.class);
    }

//    Kiểm tra email còn hạn không
    public boolean validateToken (String token){
        try{
            extractClaims(token);
            return true;
        } catch (Exception ex){
            return false;
        }
    }
}
