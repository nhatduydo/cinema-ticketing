package controller;

import dto.response.MovieResponse;
import enums.MovieStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.MovieService;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

//    Lấy tất cả phim (có phân trang)
    @GetMapping
    public ResponseEntity<Page<MovieResponse>> getAllMovies (
            @PageableDefault(size = 10, sort = "id")Pageable pageable
            ){
        return ResponseEntity.ok(movieService.getAllMovies(pageable));
    }

//    Lấy phim đang chiếu
    @GetMapping("/now-showing")
    public ResponseEntity<List<MovieResponse>> getNowShowingMovies (){
        return ResponseEntity.ok(movieService.getNowShowingMovies());
    }

//    lấy phim theo trạng thái (đang chiếu, sắp chiếu, đã kết thúc)
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<MovieResponse>> getMoviesByStatus (
            @PathVariable MovieStatus status,
            @PageableDefault (size = 10) Pageable  pageable
            ){
        return ResponseEntity.ok(movieService.getMoviesByStatus(status, pageable));
    }

//    Lấy chi tiết phim theo ID
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovieByID (@PathVariable Long id){
        return ResponseEntity.ok(movieService.getMovieById(id));
    }
}
