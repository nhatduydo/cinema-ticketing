package exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status; // Mã lỗi HTTP (400, 401, 404, 500...)
    private String message;  // Thông báo lỗi chi tiết
    private String path;     // Đường dẫn API bị lỗi
    private LocalDateTime timestamp; // Thời gian xảy ra lỗi
}
