package controller;

import dto.request.BookingRequest;
import dto.response.BookingResponse;
import dto.response.SeatAvailabilityResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    //    Xem s dồ ghế (ghế trống và đã đặt)
    public ResponseEntity<SeatAvailabilityResponse> getAvailableSeats(@PathVariable Long showtimeId) {
        return ResponseEntity.ok(bookingService.getAvailableSeats(showtimeId));
    }

    //    Đặt vé (tạo đơn thanh toán)
    @PostMapping
    public ResponseEntity<BookingResponse> createBooing(
            @Valid @RequestBody BookingRequest bookingRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(bookingService.createBooking(bookingRequest, userDetails.getUsername()));
    }

    //    Thanh toán vé
    @PostMapping("/{ticketId}/pay")
    public ResponseEntity<BookingResponse> confirmPayment(
            @PathVariable Long ticketId,
            @RequestParam String paymentMethod) {
        return ResponseEntity.ok(bookingService.confirmPayment(ticketId, paymentMethod));
    }

    //    Hủy vé (chỉ được hủy khi chưa thanh toán)
    @DeleteMapping("/{ticketId}")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bookingService.cancelBooking(ticketId, userDetails.getUsername()));
    }

//    xem lịch sử đặt vé
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @AuthenticationPrincipal UserDetails userDetails
    ){
        return ResponseEntity.ok(bookingService.getUserBookings(userDetails.getUsername()));
    }

//    Lấy QR code của ve
    @GetMapping("/{ticketId}/qrcode")
    public ResponseEntity<String> getQRCode (@PathVariable Long ticketId){
        return ResponseEntity.ok(bookingService.generateQRCode(ticketId));
    }
}
