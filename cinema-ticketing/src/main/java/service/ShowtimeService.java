package service;

import dto.request.CreateShowtimeRequest;
import dto.response.ShowtimeResponse;
import entity.CinemaHall;
import entity.Movie;
import entity.Showtime;
import exception.BookingException;
import exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import repository.CinemaHallRepository;
import repository.MovieRepository;
import repository.ShowtimeRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowtimeService {
    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final CinemaHallRepository cinemaHallRepository;

//    tạo xuất chiếu
    @Transactional
    public ShowtimeResponse createShowtime(CreateShowtimeRequest request){
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim với id: " + request.getMovieId()));
        CinemaHall hall = cinemaHallRepository.findById(request.getHallId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng chiếu với id: " + request.getHallId()));

//        Kiểm tra trùng suất chiếu (cùng phòng, thời gian gần)
        List<Showtime> existingShowtimes = showtimeRepository.findByStartTimeBetween(
                request.getStartTime().minusHours(2), // 2 giờ trước
                request.getStartTime().plusHours(2)   // 2 giờ sau
        );
        boolean conflict = existingShowtimes.stream()
                .anyMatch(s -> s.getHall().getId().equals(hall.getId()));

        if (conflict){
            throw new BookingException("Trùng suất chiếu trong cùng phòng và thời gian gần nhau");
        }

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setHall(hall);
        showtime.setStartTime(request.getStartTime());
        showtime.setEndTime(request.getEndTime());
        showtime.setBasePrice(request.getBasePrice());

        Showtime saved = showtimeRepository.save(showtime);

        return convertToResponse(saved);
    }

// Xóa suất chiếu
    @Transactional
    public void deleteShowtime(Long id){
        if (!showtimeRepository.existsById(id)){
            throw new ResourceNotFoundException("Không tìm thấy suất chiếu với id: " + id);
        }
        showtimeRepository.deleteById(id);
    }

//    Lấy suất chiếu theo ID
    @Transactional
    public ShowtimeResponse getShowtimeById (Long id){
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy suất chiếu với id: " + id));
        return convertToResponse(showtime);
    }

//   LẤY SUẤT CHIẾU THEO PHIM (7 ngày tới)
    @Transactional
    public List<ShowtimeResponse> getShowtimesByMovie(Long movieId){
        if (!movieRepository.existsById(movieId)){
            throw new ResourceNotFoundException("Không tìm thấy phim với id: " + movieId);
        }
        return showtimeRepository.findByMovieIdAndStartTimeBetween(
                movieId,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7))
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

//    Lấy suất chiếu theo ngày
    public List<ShowtimeResponse> getShowtimeByDate(LocalDate date){
        LocalDateTime startDay = date.atStartOfDay();
        LocalDateTime endDay = date.atTime(LocalTime.MAX);

        return showtimeRepository.findByStartTimeBetween(startDay, endDay)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // LẤY SUẤT CHIẾU SẮP TỚI
    public List<ShowtimeResponse> getUpcomingShowtimes() {
        return showtimeRepository.findUpcomingShowtimes(LocalDateTime.now())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    private ShowtimeResponse convertToResponse(Showtime showtime){
        return ShowtimeResponse.builder()
                .id(showtime.getId())
                .movieId(showtime.getMovie().getId())
                .movieTitle(showtime.getMovie().getTitle())
                .hallId(showtime.getHall().getId())
                .hallName(showtime.getHall().getName())
                .startTime(showtime.getStartTime())
                .endTime(showtime.getEndTime())
                .basePrice(showtime.getBasePrice())
                .build();
    }

}
