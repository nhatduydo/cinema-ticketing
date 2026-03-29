package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cinema_halls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CinemaHall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer capacity; // sức chứa của phòng chiếu || tổng số ghế trong phòng

    @Column(name = "seat_layout_json", columnDefinition = "JSON")
    private String seatLayoutJson;  // Lưu cấu trúc ghế dạng JSON

    @Column(name = "is_active")
    private Boolean isActive = true;
}
