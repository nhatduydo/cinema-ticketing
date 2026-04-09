package service;

import dto.request.BookingRequest;
import dto.response.BookingResponse;
import dto.response.SeatAvailabilityResponse;
import entity.*;
import enums.PaymentMethod;
import enums.PaymentStatus;
import enums.TicketStatus;
import exception.BookingException;
import exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repository.*;
import util.QRCodeGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final TicketRepository ticketRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PromotionRepository promotionRepository;
    private final QRCodeGenerator qrCodeGenerator;

//    Xem sơ đồ ghế (ghê trống và đã đặt)
    public SeatAvailabilityResponse getAvailableSeats(Long showtimeId){
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy suất chiếu với id: " + showtimeId));

        // ấy tất cả ghế của phòng
        List<Seat> allSeats = seatRepository.findActiveSeatsByHallId(showtime.getHall().getId());

//        Lấy danh sách ghế đã được đặt trong suất chiếu này
        List<Long> bookedSeatIds = seatRepository.findBookedSeatIdsByShowtime(showtimeId);

//        Tạo danh sách ghế kèm trạng thái
        List<SeatAvailabilityResponse.SeatInfo> seatInfos = allSeats.stream()
                .map(seat -> SeatAvailabilityResponse.SeatInfo.builder()
                        .id(seat.getId())
                        .row(seat.getSeatRow())
                        .column(seat.getSeatColumn())
                        .type(seat.getSeatType().name())
                        .price(calculateSeatPrice(showtime, seat))
                        .isAvailable(!bookedSeatIds.contains(seat.getId()))
                        .build())
                .collect(Collectors.toList());

        return SeatAvailabilityResponse.builder()
                .showtimeId(showtimeId)
                .movieTitle(showtime.getMovie().getTitle())
                .hallName(showtime.getHall().getName())
                .startTime(showtime.getStartTime())
                .seats(seatInfos)
                .build();
    }

//    Tạo đơn đặt vé (chưa thanh toán)
    @Transactional
    public BookingResponse createBooking(BookingRequest request, String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với email: " + userEmail));

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy suất chiếu với id): " + request.getShowtimeId()));

//        Kiểm tra suất chiếu đã qua chưa
        if (showtime.getStartTime().isBefore(LocalDateTime.now())){
            throw new BookingException("Không thể đặt vé cho suất chiếu đã bắt đầu");
        }

//        Kiểm tra ghế còn trống không
        List<Long> bookedSeats = seatRepository.findBookedSeatIdsByShowtime(request.getShowtimeId());
        if(bookedSeats.contains(request.getSeatId())){
            throw new BookingException("Một hoặc nhiều ghế đã được đặt, vui lòng chọn ghế khác");
        }

        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ghế với id: " + request.getSeatId()));

//        Tính giá vé
        BigDecimal price = calculateSeatPrice(showtime, seat);

//        Áp dụng khuyến mãi nếu có
        if (request.getPromotionCode() != null && request.getPromotionCode().isEmpty()){
            Promotion promotion = promotionRepository.findByCode(request.getPromotionCode())
                    .orElseThrow(() -> new BookingException("Mã khuyến mãi không hợp lệ"));

            price = applyPromotion(price, promotion);
            promotion.setUsedCount(promotion.getUsedCount() + 1);
            promotionRepository.save(promotion);
        }

//        Tạo vé
        Ticket ticket = new Ticket();
        ticket.setShowtime(showtime);
        ticket.setSeat(seat);
        ticket.setUser(user);
        ticket.setPrice(price);
        ticket.setStatus(TicketStatus.PENDING); // trạng thái ban đầu là BOOKED
        Ticket savedTicket = ticketRepository.save(ticket);

        return convertToBookingResponse(savedTicket);
    }

//    Thanh toán vé
    @Transactional
    public BookingResponse confirmPayment (Long ticketId, String paymentMethod) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vé với id: " + ticketId));

        if (ticket.getStatus() != TicketStatus.PENDING) {
            throw new BookingException("Chỉ có thể thanh toán cho vé đang chờ xử lý");
        }

//        Kiểm tra suất chiếu đã qua chưa
        if (ticket.getShowtime().getStartTime().isBefore(LocalDateTime.now())) {
            throw new BookingException("Không thể thanh toán cho suất chiếu đã bắt đầu");
        }

        // Kiểm tra suất chiếu đã qua chưa
        if (ticket.getShowtime().getStartTime().isBefore(LocalDateTime.now())) {
            ticket.setStatus(TicketStatus.EXPIRED);
            ticketRepository.save(ticket);
            throw new BookingException("Suất chiếu đã bắt đầu, vé đã hết hạn");
        }

//        Cập nhập trạng thái vé
        ticket.setStatus(TicketStatus.PAID);
        ticket.setPaymentTime(LocalDateTime.now());
        ticketRepository.save(ticket);

//        Tạo bản ghi thanh toán
        Payment payment = new Payment();
        payment.setTicket(ticket);
        payment.setAmount(ticket.getPrice());
        payment.setPaymentMethod(PaymentMethod.valueOf(paymentMethod.toUpperCase()));
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        // Tạo QR code
        String qrCode = qrCodeGenerator.generateQRCode(ticket.getCode());

        return convertToBookingResponse(ticket, qrCode);

    }

//    Hủy vé (chỉ hủy đươc khi chưa thanh toán)
    @Transactional
    public BookingResponse cancelBooking (Long tickdetId, String userEmail){
        Ticket ticket = ticketRepository.findById(tickdetId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vé với id: " + tickdetId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với email: " + userEmail));

        if (!ticket.getUser().getId().equals(user.getId())){
            throw new BookingException("Bạn không có quyền hủy vé này");
        }

        if (ticket.getStatus() != TicketStatus.PENDING){
            throw new BookingException("Chỉ có thể hủy vé chưa thanh toán");
        }

        ticket.setStatus(TicketStatus.CANCELLED);
        ticketRepository.save(ticket);

        return  convertToBookingResponse(ticket);
    }

//    Lấy lịch sử đặt vé của user
    public List<BookingResponse> getUserBookings (String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với email: " + userEmail));

        return ticketRepository.findById(user.getId()).stream()
                .map(this::convertToBookingResponse)
                .collect(Collectors.toList());
    }

//    Tạo QR code cho vé
    public String generateQRCode(Long ticketId){
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vé với id: " + ticketId));

        return qrCodeGenerator.generateQRCode(ticket.getCode());
    }

//    Tính giá vé (theo loại ghế, ngày cuối tun, khung giờ)
    private BigDecimal calculateSeatPrice(Showtime showtime, Seat seat){
        BigDecimal basePrice = showtime.getBasePrice();

//        Hệ số theo loại ghế
        BigDecimal typeMultiplier = switch (seat.getSeatType()){
            case VIP -> BigDecimal.valueOf(1.5);
            case COUPLE -> BigDecimal.valueOf(1.2);
            default -> BigDecimal.ONE;
        };

//        Hệ số theo ngày (Cuối tuần tăng 20%)
        LocalDateTime startTime = showtime.getStartTime();
        boolean isWeekend = startTime.getDayOfWeek().getValue() >= 6;
        BigDecimal weekendMultipler = isWeekend ? BigDecimal.valueOf(1.2) : BigDecimal.ONE;

//        hệ số theo khung giờ (tối từ 18h - 22h tăng 10%)
        int hour = startTime.getHour();
        BigDecimal timeMultiplier = (hour >= 18 && hour <= 22) ? BigDecimal.valueOf(1.1) : BigDecimal.ONE;

        return basePrice.multiply(typeMultiplier)
                .multiply(weekendMultipler)
                .multiply(timeMultiplier);
    }

//    Áp dụng khuyến mãi
    private BigDecimal applyPromotion(BigDecimal price, Promotion promotion) {
        return switch (promotion.getDiscountType()) {
            case PERCENT -> price.multiply(BigDecimal.ONE.subtract(
                    promotion.getDiscountValue().divide(BigDecimal.valueOf(100))));
            case FIXED -> price.subtract(promotion.getDiscountValue()).max(BigDecimal.ZERO);
        };
    }
    // Chuyển đổi Ticket thành BookingResponse (không có QR code)
    private BookingResponse convertToBookingResponse(Ticket ticket) {
        return convertToBookingResponse(ticket, null);
    }

    // Chuyển đổi Ticket thành BookingResponse (có QR code)
    private BookingResponse convertToBookingResponse(Ticket ticket, String qrCode) {
        return BookingResponse.builder()
                .id(ticket.getId())
                .code(ticket.getCode())
                .movieTitle(ticket.getShowtime().getMovie().getTitle())
                .hallName(ticket.getShowtime().getHall().getName())
                .startTime(ticket.getShowtime().getStartTime())
                .seatRow(ticket.getSeat().getSeatRow())
                .seatColumn(ticket.getSeat().getSeatColumn())
                .seatType(ticket.getSeat().getSeatType().name())
                .price(ticket.getPrice())
                .status(ticket.getStatus().name())
                .bookingTime(ticket.getBookingTime())
                .qrCode(qrCode)  // QR code có thể null
                .build();
    }
}
