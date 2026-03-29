package entity;

import enums.SeatType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "seats")
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hall_id")
    private CinemaHall hall; // Phòng chiếu phim trong rạp

    @Column(name = "seat_row")
    private String seatRow;

    @Column(name = "seat_column")
    private Integer seatColumn;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type")
    private SeatType seatType = SeatType.STANDARD;

    @Column(name = "price_multiplier")
    private BigDecimal priceMultiplier = BigDecimal.ONE; // hệ số giá

    @Column(name = "is_active")
    private Boolean isActive = true;
}
