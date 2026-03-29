# cinema-ticketing
cinema-ticketing
Project for cinema ticketing system.

# list of functions:
## 1. User: 
+ Register
+ Login
+ Forgot password

## 2. Booking:
+ select showtime (chọn suất chiếu)
+ select seat (chọn ghế)
+ payment (thanh toán) [Not through a third part]

Display
+ Seats have been reserved (ghế đã được đặt)
+ Seats are available (ghế còn trống)
+ Ticket prices vary by seat type (giá vé khác nhau tùy theo loại ghế)
+ View booking history (xem lịch sử đặt vé)
+ Cancel booking (hủy đặt vé)

## 3. Admin:
+ Add movie (thêm phim)
+ Edit movie (sửa phim)
+ Delete movie (xóa phim)
+ View bookings (xem đặt vé)
+ Generate reports (tạo báo cáo)
+ Manage users (quản lý người dùng)
+ View user list (xem danh sách người dùng)
+ Showtime management (quản lý suất chiếu)
+ Add showtime (thêm suất chiếu)
+ Edit showtime (sửa suất chiếu)
+ Delete showtime (xóa suất chiếu)
+ Screening time management (quản lý thời gian chiếu)
+ Add screening time (thêm thời gian chiếu)
+ Edit screening time (sửa thời gian chiếu)
+ Delete screening time (xóa thời gian chiếu)
+ Ticket price management (quản lý giá vé)
+ Set ticket price (đặt giá vé)
+ Edit ticket price (sửa giá vé)
+ Delete ticket price (xóa giá vé)
+ Seat management (quản lý ghế)
+ Daily revenue report (báo cáo doanh thu hàng ngày)
+ Monthly revenue report (báo cáo doanh thu hàng tháng)
+ Yearly revenue report (báo cáo doanh thu hàng năm)
+ Revenue by Film report (báo cáo doanh thu theo phim)

---
# project directory tree
cinema-ticketing/
## Project Structure

```bash
cinema-ticketing/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── cinema/
│       │           └── ticketing/
│       │               ├── CinemaTicketingApplication.java
│       │
│       │               ├── entity/
│       │               │   ├── User.java
│       │               │   ├── Movie.java
│       │               │   ├── CinemaHall.java
│       │               │   ├── Showtime.java
│       │               │   ├── Seat.java
│       │               │   ├── Ticket.java
│       │               │   ├── Payment.java
│       │               │   └── Promotion.java
│       │
│       │               ├── dto/
│       │               │   ├── request/
│       │               │   │   ├── LoginRequest.java
│       │               │   │   ├── RegisterRequest.java
│       │               │   │   ├── ForgotPasswordRequest.java
│       │               │   │   ├── ResetPasswordRequest.java
│       │               │   │   ├── BookingRequest.java
│       │               │   │   ├── CreateMovieRequest.java
│       │               │   │   └── CreateShowtimeRequest.java
│       │               │   └── response/
│       │               │       ├── AuthResponse.java
│       │               │       ├── MovieResponse.java
│       │               │       ├── SeatAvailabilityResponse.java
│       │               │       ├── BookingResponse.java
│       │               │       ├── RevenueReportResponse.java
│       │               │       ├── ShowtimeResponse.java
│       │               │       └── PaymentResponse.java
│       │
│       │               ├── repository/
│       │               │   ├── UserRepository.java
│       │               │   ├── MovieRepository.java
│       │               │   ├── CinemaHallRepository.java
│       │               │   ├── ShowtimeRepository.java
│       │               │   └── SeatRepository.java
│       │               │   ├── TicketRepository.java
│       │               │   └── PaymentRepository.java
│       │               │   └── PromotionRepository.java
│       │
│       │               ├── service/
│       │               │   ├── AuthService.java
│       │               │   ├── MovieService.java
│       │               │   ├── ShowtimeService.java
│       │               │   ├── BookingService.java
│       │               │   ├── PaymentService.java
│       │               │   ├── StatisticsService.java
│       │               │   └── impl/
│       │               │       ├── AuthServiceImpl.java
│       │               │       ├── MovieServiceImpl.java
│       │               │       ├── ShowtimeServiceImpl.java
│       │               │       ├── BookingServiceImpl.java
│       │               │       ├── PaymentServiceImpl.java
│       │               │       └── StatisticsServiceImpl.java
│       │
│       │               ├── controller/
│       │               │   ├── DiscountType.java
│       │               │   ├── MovieStatus.java
│       │               │   ├── PaymentMethod.java
│       │               │   ├── PaymentStatus.java
│       │               │   └── SeatType.java
│       │               │   ├── TicketStatus.java
│       │               │   └── UserRole.java
│       │
│       │               ├── config/
│       │               │   ├── SecurityConfig.java
│       │               │   ├── JwtUtil.java
│       │               │   ├── JwtFilter.java
│       │               │   ├── WebConfig.java
│       │               │   └── FileUploadConfig.java
│       │
│       │               ├── exception/
│       │               ├── enums/
│       │               │   ├── DiscountType.java
│       │               │   ├── MovieStatus.java
│       │               │   ├── PaymentMethod.java
│       │               │   ├── PaymentStatus.java
│       │               │   └── SeatType.java
│       │               │   ├── TicketStatus.java
│       │               │   └── UserRole.java
│       │               ├── util/
│       │
│       │               └── scheduler/
│       │                   └── TicketExpirationScheduler.java
│       │
│       └── resources/
│           ├── application.properties
│           └── static/
│               └── posters/
│
├── uploads/
└── posters/
```
---
# dependency list
```bash
STT	Dependency	                            Chức năng (1 dòng)
1	spring-boot-starter-web	                Xây dựng REST API
2	spring-boot-starter-data-jpa	        Thao tác database với Hibernate
3	spring-boot-starter-security	        Xác thực và phân quyền
4	spring-boot-starter-validation	        Kiểm tra dữ liệu đầu vào
5	mysql-connector-j	                    Kết nối MySQL
6	lombok	                                Giảm code thừa (getter/setter)
7	spring-boot-configuration-processor	    Gợi ý cấu hình trong properties
8	jjwt-api	                            Tạo và xác thực JWT
9	jjwt-impl	                            Phần cài đặt JWT
10	jjwt-jackson	                        Hỗ trợ JWT với JSON
11	zxing-core	                            Tạo mã QR code
12	zxing-javase	                        Xử lý ảnh QR code
13	spring-boot-starter-test	            Viết test
14	spring-security-test	                Test bảo mật
```
# Step-by-step coding roadmap
1. Entity (định nghĩa cấu trúc bảng)
2. Enum (các hằng số như role, status...)
3. Repository (truy vấn database)
4. DTO (dữ liệu gửi đi/nhận về)
5. Service (logic nghiệp vụ)
6. Controller (API endpoints)
7. Config (bảo mật, JWT)
8. Util (QR code, upload file)
---
## Giải thích annotation trong DTO
```bash
Annotation	        Ý nghĩa
@Data	            Lombok: tự động sinh getter, setter, toString
@Builder	        Lombok: tạo pattern Builder để tạo object dễ dàng
@NotBlank	        Validation: không được null hoặc khoảng trắng
@Email	            Validation: phải đúng định dạng email
@Size(min = 6)	    Validation: độ dài tối thiểu 6 ký tự
@NotNull	        Validation: không được null
```
---
## So sánh Entity vs DTO
```bash
Tiêu chí	              Entity	                DTO
Mục đích	              Mapping với database	    Giao tiếp với client
Có password không?	      Có	                    Không
Có quan hệ @ManyToOne?	  Có	                    Không
Có validation?	          Không	                    Có (@NotBlank, @Email...)
Gửi ra API?	              Không bao giờ	            Có
```
---
