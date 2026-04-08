package controller;

import dto.request.ForgotPasswordRequest;
import dto.request.LoginRequest;
import dto.request.RegisterRequest;
import dto.request.ResetPasswordRequest;
import dto.response.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    //    Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login (@Valid @RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    // Đăng ký
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> resgister (@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

//    Quên mật khẩu - gửi email reset
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword (@Valid @RequestBody ForgotPasswordRequest request){
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("Email hướng dẫn đặt lại mật khẩu đã được gửi");
    }

//    Đặt lại mật khẩu
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword (@Valid @RequestBody ResetPasswordRequest request){
       authService.resetPassword(request.getToken(), request.getNewPassword());
       return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công");
    }
}
