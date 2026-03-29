package repository;

import entity.Movie;
import enums.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
//    get list movie by status (has pagination)
    Page<Movie> findByStatus(MovieStatus status, Pageable pageable);

//    get list movie by status (no pagination)
    List<Movie> findByStatus(MovieStatus status);

//    check if the movie title already exists
    boolean existsByTitle(String title);
}
