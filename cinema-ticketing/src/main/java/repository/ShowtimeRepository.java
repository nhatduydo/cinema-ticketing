package repository;

import entity.Showtime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<entity.Showtime, Long> {
//    Find showtimes by movie and time slot
    List<Showtime> findByMovieIdAndStartTimeBetween(Long movieId, LocalDateTime start, LocalDateTime end);

//    Find showtimes within a given time range (Tìm suất chiếu trong khoảng thời gian)
    List<Showtime> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

//    Find upcoming (not yet scheduled) screenings (Tìm suất chiếu sắp tới (chưa diễn ra))
    @Query("SELECT s FROM Showtime s WHERE s.startTime > :now ORDER BY s.startTime ASC")
    List<Showtime> findUpcomingShowtimes(@Param("now") LocalDateTime now);

//    Count the number of tickets sold for the screening (Đếm số vé đã bán của suất chiếu)
    @Query("SELECT COUNT(*) FROM Ticket t WHERE t.showtime.id = :showtimeId AND t.status != 'CANCELLED'")
    Long countBookedTickets(@Param("showtimeId") Long showtimeId);
}

