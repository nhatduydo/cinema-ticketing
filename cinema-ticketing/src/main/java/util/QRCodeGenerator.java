package util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Component
public class QRCodeGenerator {
    /**
     * Tạo mã QR code từ nội dung
     * @param content Nội dung cần mã hóa (thường là mã vé)
     * @return Chuỗi base64 của ảnh QR code (có thể nhúng trực tiếp vào HTML)
     */

    public String generateQRCode (String content){
        try{
//            Tạo QR code với kích thước 300x300
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, com.google.zxing.BarcodeFormat.QR_CODE, 300, 300);

            // Chuyển ảnh thành mảng byte
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            // Chuyển thành base64 để dễ dàng gửi qua JSON
            String base64Image = Base64.getEncoder().encodeToString(outputStream.toByteArray());

            // Trả về data URL để hiển thị trực tiếp trong trình duyệt
            return "data:image/png;base64," + base64Image;

        } catch (WriterException | IOException e){
            throw new RuntimeException("Lỗi khi tạo mã QR code: " + e.getMessage(), e);
        }
    }

    /**
     * Tạo QR code với kích thước tùy chỉnh
     * @param content Nội dung cần mã hóa
     * @param width Chiều rộng
     * @param height Chiều cao
     * @return Chuỗi base64 của ảnh QR code
     */

    public String generateQRCode(String content, int width, int height){
        try{
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            String base64Image = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            return "data:image/png;base64," + base64Image;
        } catch ( WriterException | IOException e){
            throw new RuntimeException("Lỗi tạo mã QR code: " + e.getMessage(), e);
        }
    }
}
