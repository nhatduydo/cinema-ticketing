package service;

import dto.request.CreateMovieRequest;
import dto.response.MovieResponse;
import entity.Movie;
import enums.MovieStatus;
import exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import repository.MovieRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;

//    Chuyển entity thành DTO
    private MovieResponse convertToResponse(Movie movie){
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .duration(movie.getDuration())
                .status(movie.getStatus())
                .releaseDate(movie.getReleaseDate())
                .posterUrl(movie.getPosterUrl())
                .build();
    }

//    Tạo phim mới
    @Transactional
    public MovieResponse createMovie (CreateMovieRequest request, MultipartFile poster){
//        kiểm tra tên phim đã tồn tại
        if (movieRepository.existsByTitle(request.getTitle())){
            throw new ResourceNotFoundException("Tên phim đã tồn tại");
        }

        Movie movie = new Movie();
        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setDuration(request.getDuration());
        movie.setStatus(request.getStatus() != null ? request.getStatus() : MovieStatus.COMING_SOON); // mặc định là COMING_SOON nếu không có input
        movie.setReleaseDate(request.getReleaseDate());

//        upload poster nếu có
        if (poster != null & !poster.isEmpty()){
            String posterUrl = savePoster(poster);
            movie.setPosterUrl(posterUrl);
        }

        Movie saved = movieRepository.save(movie);
        return convertToResponse(saved);
    }

//    Cập nhật thông tin phim
    @Transactional
    public MovieResponse updatedMovie (Long id, CreateMovieRequest request, MultipartFile poster){
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim với id: " + id));

        if (!movie.getTitle().equals(request.getTitle()) && movieRepository.existsByTitle(request.getTitle())){
            throw new ResourceNotFoundException("Tên phim đã tồn tại");
        }

        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setDuration(request.getDuration());
        movie.setStatus(request.getStatus() != null ? request.getStatus() : MovieStatus.COMING_SOON);
        movie.setReleaseDate(request.getReleaseDate());

        if (poster != null && !poster.isEmpty()){
            String posterUrl = savePoster(poster);
            movie.setPosterUrl(posterUrl);
        }

        Movie updated = movieRepository.save(movie);
        return convertToResponse(updated);
    }

    // XÓA PHIM
    @Transactional
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy phim với id: " + id);
        }
        movieRepository.deleteById(id);
    }

    // LẤY PHIM THEO ID
    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim với id: " + id));
        return convertToResponse(movie);
    }

    // LẤY TẤT CẢ PHIM (CÓ PHÂN TRANG)
    public Page<MovieResponse> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable).map(this::convertToResponse);
    }

    // LẤY PHIM THEO TRẠNG THÁI
    public Page<MovieResponse> getMoviesByStatus(MovieStatus status, Pageable pageable) {
        return movieRepository.findByStatus(status, pageable).map(this::convertToResponse);
    }

    // lấy phim đang chiếu
    public List<MovieResponse> getNowShowingMovies(){
        return movieRepository.findByStatus(MovieStatus.NOW_SHOWING)
                .stream()// Chuyển List → Stream để xử lý
                .map(this::convertToResponse)
                .collect(Collectors.toList()); // Gom lại thành List
    }

    // savePoster
    private String savePoster(MultipartFile file){
        try{
            String uploadDir = "./uploads/posters/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            return "/uploads/posters/" + fileName;
        } catch (IOException e){
            throw new RuntimeException("Lỗi khi lưu poster: " + e.getMessage());
        }
    }
}
