package dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponse {
    private Long id;
    private String code;           // Mã vé
    private String movieTitle;
    private String hallName;
    private LocalDateTime startTime;
    private String seatRow;
    private Integer seatColumn;
    private String seatType;
    private BigDecimal price;
    private String status;         // PENDING, PAID, CANCELLED, EXPIRED
    private LocalDateTime bookingTime;
    private String qrCode;         // Mã QR dạng base64
}
