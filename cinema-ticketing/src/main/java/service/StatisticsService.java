package service;

import dto.response.RevenueReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repository.TicketRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final TicketRepository ticketRepository;

//    Báo cáo doanh thu tổng hợp
    public RevenueReportResponse getRevenueReport (LocalDateTime startDate, LocalDateTime endDate){
        Double revenue = ticketRepository.sumRevenueByDateRange(startDate, endDate);
        Long ticketCount = ticketRepository.countPaidTicketsByDateRange(startDate, endDate);

        return RevenueReportResponse.builder()
                .totalRevenue(revenue != null ? BigDecimal.valueOf(revenue) : BigDecimal.ZERO)
                .totalTicketsSold(ticketCount != null ? ticketCount : 0L)
                .startDate(startDate.toLocalDate())
                .endDate(endDate.toLocalDate())
                .dailyRevenues(getDailyRevenue(startDate.toLocalDate(), endDate.toLocalDate()))
                .movieRevenues(getRevenueByMovie(startDate, endDate))
                .build();
    }


    // DOANH THU THEO NGÀY
    public List<RevenueReportResponse.DailyRevenue> getDailyRevenue(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Object[]> results = ticketRepository.getDailyRevenue(start, end);

        return results.stream()
                .map(row -> RevenueReportResponse.DailyRevenue.builder()
                        .date(((java.sql.Date) row[0]).toLocalDate())
                        .revenue(BigDecimal.valueOf((Double) row[1]))
                        .ticketCount((Long) row[2])
                        .build())
                .collect(Collectors.toList());
    }

    // DOANH THU THEO PHIM
    public List<RevenueReportResponse.MovieRevenue> getRevenueByMovie(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = ticketRepository.getRevenueByMovie(startDate, endDate);

        return results.stream()
                .map(row -> RevenueReportResponse.MovieRevenue.builder()
                        .movieTitle((String) row[0])
                        .revenue(BigDecimal.valueOf((Double) row[1]))
                        .ticketCount((Long) row[2])
                        .build())
                .collect(Collectors.toList());
    }
}
