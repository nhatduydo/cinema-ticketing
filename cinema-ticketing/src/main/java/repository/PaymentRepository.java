package repository;

import entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
//    TÌm thanh toán theo vé
    Optional<Payment> findByTicketId(Long ticketId);
}
