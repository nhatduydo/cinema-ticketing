package dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class RevenueReportResponse {
    private BigDecimal totalRevenue;
    private Long totalTicketsSold;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<DailyRevenue> dailyRevenues;
    private List<MovieRevenue> movieRevenues;

    @Data
    @Builder
    public static class DailyRevenue {
        private LocalDate date;
        private BigDecimal revenue;
        private Long ticketCount;
    }

    @Data
    @Builder
    public static class MovieRevenue {
        private String movieTitle;
        private BigDecimal revenue;
        private Long ticketCount;
    }

}
