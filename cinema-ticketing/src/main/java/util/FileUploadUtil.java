package util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileUploadUtil {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveFile(MultipartFile file, String subDir) throws IOException{
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Tạo tên file duy nhất
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;

        // Tạo đường dẫn đầy đủ
        Path uploadPath = Paths.get(uploadDir, subDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Lưu file
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        // Trả về URL để truy cập
        return "/uploads/" + subDir + "/" + fileName;
    }

    /**
     * Xóa file
     * @param fileUrl Đường dẫn URL của file cần xóa
     */

    public void deleteFile(String fileUrl) throws IOException{
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        // Chuyển URL thành đường dẫn file
        String relativePath = fileUrl.substring(fileUrl.indexOf("/uploads/") + 9);
        Path filePath = Paths.get(uploadDir, relativePath);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
}
