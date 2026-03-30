package service;

import config.JwtUtil;
import dto.request.LoginRequest;
import dto.request.RegisterRequest;
import dto.response.AuthResponse;
import entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor //tự động tạo constructor (hàm khởi tạo) cho class.
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

//    login
    public AuthResponse login(LoginRequest request){
//        1. find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email hoặc mật khẩu không đúng"));

//        2. check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        }

//        3. generate token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

//        4. return response
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

//    Đăng ký
    @Transactional
    public AuthResponse register(RegisterRequest request){
//        1. kiểm tra email tồn tại chưa
        if (userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email đã được đăng ký");
        }

//        2. tạo user mới
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // mã hóa mật khẩu
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        userRepository.save(user);

        userRepository.save(user);
//        3. generate token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

//    Quên mật khẩu - gửi email reset
    @Transactional
    public void forgotPassword (String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

//        Tạo reset password
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1)); // token có hiệu lực trong 1 giờ
        userRepository.save(user);

//        gửi email chứa link rết password
        System.out.println("Gửi email reset mật khẩu đến " + email + " với token: " + resetToken);
        System.out.println("Link reset mật khẩu: http://localhost:8080/api/auth/reset-password?token=" + resetToken);
    }
    //        Đặt lại mật khẩu
    @Transactional
    public void resetPassword (String token, String newPassword){
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Token đã hết hạn");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }
}
