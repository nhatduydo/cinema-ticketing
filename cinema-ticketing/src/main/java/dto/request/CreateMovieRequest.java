package dto.request;

import enums.MovieStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateMovieRequest {
    @NotBlank(message = "Tên phim không được để trống") // Dùng cho String, kiểm tra không null, không rỗng và không chỉ chứa khoảng trắng
    private String title;

    private String description;

    @NotNull(message = "Thời lượng không được để trống") //Chỉ kiểm tra không được null (có thể rỗng "" vẫn hợp lệ)
    private Integer duration;

    private MovieStatus status;

    private LocalDate releaseDate;
}
