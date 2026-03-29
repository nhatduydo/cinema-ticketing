package dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder // Tạo object theo kiểu builder giúp khởi tạo dễ đọc, không cần constructor dài
public class AuthResponse {
    private String token; // JWT token
    private Long userId; // ID của user
    private String username; // Tên đăng nhập
    private String email;
    private String role; // ADMIN or USER

}
