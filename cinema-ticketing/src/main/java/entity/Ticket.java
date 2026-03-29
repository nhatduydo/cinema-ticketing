package entity;

import enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tickets")
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @ManyToOne
    @JoinColumn(name = "showtime_id")
    private Showtime showtime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private TicketStatus status = TicketStatus.PENDING;

    @Column(name = "booking_time")
    private LocalDateTime bookingTime;

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @PrePersist
    protected void onCreate() {
        bookingTime = LocalDateTime.now();
        code = generateTicketCode();
    }

    private String generateTicketCode() {
        return "TICKET" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
    }
}
