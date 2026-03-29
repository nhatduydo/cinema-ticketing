package repository;

import entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
//        Take all the chairs in a room - Lấy tất cả ghế của một phòng
    List<Seat> findByHallId(Long hallId);

//    Get the active chairs of a room (arranged in rows and columns)
//    Lấy ghế đang hoạt động của một phòng (sắp xếp theo hàng và cột)
    @Query("SELECT s FROM Seat s WHERE s.hall.id = :hallId AND s.isActive = true ORDER BY s.seatRow, s.seatColumn")
    List<Seat> findActiveSeatsByHallId(@Param("hallId") Long hallId);

//    Get the of seat IDs that have been reserved for the screening - Lấy danh sách ID ghế đã được đặt trong suất chiếu
    @Query("SELECT t.seat.id FROM Ticket t WHERE t.showtime.id = :showtimeId AND t.status != 'CANCELLED'")
    List<Long> findBookedSeatIdsByShowtime(@Param("showtimeId") Long showtimeId);

//    Find seats by room, row, and column - Tìm ghế theo phòng, hàng và cột
Seat findByHallIdAndSeatRowAndSeatColumn(Long hallId, String seatRow, Integer seatColumn);
}
