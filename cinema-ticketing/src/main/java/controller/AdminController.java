package controller;

import dto.request.CreateMovieRequest;
import dto.request.CreateShowtimeRequest;
import dto.response.MovieResponse;
import dto.response.RevenueReportResponse;
import dto.response.ShowtimeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.MovieService;
import service.ShowtimeService;
import service.StatisticsService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // Chỉ cho phép truy cập nếu có vai trò ADMIN
public class AdminController {

    private final MovieService movieService;
    private final ShowtimeService showtimeService;
    private final StatisticsService statisticsService;

//    quản lý phim
//    Thêm phim mới
    @PostMapping("/movies")
    public ResponseEntity<MovieResponse> createMovie (
            @Valid @RequestPart("movie")CreateMovieRequest createMovieRequest,
            @RequestPart(value = "poster", required = false) MultipartFile poster
            ){
        return ResponseEntity.ok(movieService.createMovie(createMovieRequest, poster));
    }

//    Xóa phim
    @DeleteMapping("/movies/{id}")
    public ResponseEntity<String> deleteMovie(@PathVariable Long id){
        movieService.deleteMovie(id);
        return  ResponseEntity.ok("Phim đã được xóa thành công");
    }

//    Tạo suất chiếu mới
    @PostMapping("/showtimes")
    public ResponseEntity<ShowtimeResponse> createShowtime (@Valid @RequestBody CreateShowtimeRequest request){
        return ResponseEntity.ok(showtimeService.createShowtime(request));
    }

//    Xóa suất chiếu
    @DeleteMapping("/showtimes/{id}")
    public ResponseEntity<String> deleteShowtime(@PathVariable Long id) {
        showtimeService.deleteShowtime(id);
        return ResponseEntity.ok("Xóa suất chiếu thành công");
    }

//    THống kê và báo cáo
//    Báo cáo doanh thu tổng hợp
    @GetMapping("/statistics/revenue")
    public ResponseEntity<RevenueReportResponse> getRevenueReport (
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDate
    ){
        return ResponseEntity.ok(statisticsService.getRevenueReport(startDate, endDate));
    }

    // DOANH THU THEO NGÀY
    @GetMapping("/statistics/revenue/daily")
    public ResponseEntity<List<RevenueReportResponse.DailyRevenue>> getDailyRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(statisticsService.getDailyRevenue(startDate, endDate));
    }

    // DOANH THU THEO PHIM
    @GetMapping("/statistics/revenue/by-movie")
    public ResponseEntity<List<RevenueReportResponse.MovieRevenue>> getRevenueByMovie(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(statisticsService.getRevenueByMovie(startDate, endDate));
    }
}
