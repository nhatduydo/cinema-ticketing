package repository;

import entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    // Tìm khuyến mãi theo mã
    Optional<Promotion> findByCode(String code);

    // Tìm khuyến mãi còn hiệu lực (chưa hết hạn, chưa dùng hết số lần)
    @Query("SELECT p FROM Promotion p WHERE p.code = :code AND p.startDate <= :now  AND p.endDate >= :now  AND p.usedCount < p.maxUses")
    Optional<Promotion> findValidPromotion(@Param("code") String code, @Param("now") java.time.LocalDateTime now);
}
