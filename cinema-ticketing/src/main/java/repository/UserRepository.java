package repository;

import entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//    find user by email (used for login)
//    Optional<User> nghĩa là: có thể có User hoặc không
    Optional<User> findByEmail(String email);

//    check if the email address already exists (used for registration)
    boolean existsByEmail(String email);

//    Find user by reset token (used for forgotten password)
    Optional<User> findByResetToken(String resetToken);

}
