package controller;

import dto.response.ShowtimeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.ShowtimeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/showtimes")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

//    Lấy suât chiếu theo phim
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ShowtimeResponse>> getShowtimesByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(showtimeService.getShowtimesByMovie(movieId));
    }

// Lấy suât chiếu sắp tới
    @GetMapping("/upcoming")
    public ResponseEntity<List<ShowtimeResponse>> getUpcomingShowtimes (){
        return ResponseEntity.ok(showtimeService.getUpcomingShowtimes());
    }

//    Lấy chi tiết suát chiếu theo id
    @GetMapping("/{id}")
    public ResponseEntity<ShowtimeResponse> getShowtimeById (@PathVariable Long id){
        return ResponseEntity.ok(showtimeService.getShowtimeById(id));
    }
}
