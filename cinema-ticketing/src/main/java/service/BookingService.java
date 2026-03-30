package service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repository.*;

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


}
