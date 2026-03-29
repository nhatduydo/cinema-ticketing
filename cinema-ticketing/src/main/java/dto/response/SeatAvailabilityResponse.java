package dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SeatAvailabilityResponse {
    private Long showtimeId;
    private String movieTitle;
    private String hallName;
    private LocalDateTime startTime;
    private List<SeatInfo> seats;

    @Data
    @Builder
    public static class SeatInfo {
        private Long id;
        private String row;       // Hàng ghế: A, B, C...
        private Integer column;   // Cột ghế: 1, 2, 3...
        private String type;      // STANDARD, VIP, COUPLE
        private BigDecimal price;
        private Boolean isAvailable;  // true = còn trống, false = đã đặt
    }
}
