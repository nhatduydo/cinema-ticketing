package repository;

import entity.Ticket;
import enums.TicketStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    //Search for tickets by user -  Tìm vé theo user
    List<Ticket> findByUserId(Long userId);

    //Search for tickets by user (with pagination) - Tìm vé theo user (có phân trang)
    Page<Ticket> findByUserId(Long userId, Pageable pageable);

    // Search for tickets by showtime and status - Tìm vé theo suất chiếu và trạng thái
    List<Ticket> findByShowtimeIdAndStatus(Long showtimeId, TicketStatus status);

//    Find tickets by ticket code - Tìm vé theo mã vé
    Optional<Ticket> findByCode(String code);

    // Tickets awaiting payment (regular service) have expired.
    // Hết hạn các vé đang chờ thanh toán (chạy định kỳ)
    @Modifying // Đánh dấu query là UPDATE/DELETE (có thay đổi dữ liệu)
    @Transactional  // Đảm bảo query chạy trong transaction (có thể rollback nếu lỗi)
    @Query("UPDATE Ticket t SET t.status = 'EXPIRED' WHERE t.status = 'PENDING' AND t.bookingTime < :expiryTime")
    int expirePendingTickets(@Param("expiryTime") LocalDateTime expiryTime);

    // Total revenue during the period - Tổng doanh thu trong khoảng thời gian
    @Query("SELECT SUM(t.price) FROM Ticket t WHERE t.status = 'PAID' AND t.paymentTime BETWEEN :startDate AND :endDate")
    Double sumRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

//    Count the number of tickets sold within a given time period
    // Đếm số vé đã bán trong khoảng thời gian
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = 'PAID' AND t.paymentTime BETWEEN :startDate AND :endDate")
    Long countPaidTicketsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Doanh thu theo ngày
    @Query("SELECT FUNCTION('DATE', t.paymentTime), SUM(t.price), COUNT(t) FROM Ticket t " +
            "WHERE t.status = 'PAID' AND t.paymentTime BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('DATE', t.paymentTime) ORDER BY FUNCTION('DATE', t.paymentTime)")
    List<Object[]> getDailyRevenue(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Doanh thu theo phim
    @Query("SELECT t.showtime.movie.title, SUM(t.price), COUNT(t) FROM Ticket t " +
            "WHERE t.status = 'PAID' AND t.paymentTime BETWEEN :startDate AND :endDate " +
            "GROUP BY t.showtime.movie.title ORDER BY SUM(t.price) DESC")
    List<Object[]> getRevenueByMovie(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
