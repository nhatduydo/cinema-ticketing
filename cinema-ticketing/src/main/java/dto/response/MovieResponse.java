package dto.response;

import enums.MovieStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class MovieResponse {
    private Long id;
    private String title;
    private String description;
    private Integer duration;
    private String posterUrl;
    private MovieStatus status;
    private LocalDate releaseDate;
}
