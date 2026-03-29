package dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequest {
    @NotNull(message = "ID suất chiu không được để trống")
    private Long showtimeId;

    @NotNull(message = "ID ghế không được để trống")
    private Long seatId;

    private String promotionCode;  // Mã khuyến mãi (không bắt buộc)
}
