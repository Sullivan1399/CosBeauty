package vn.cosbeauty.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class FileService {
	
	private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
		File tempFile = new File(fileName);
		try (FileOutputStream fos = new FileOutputStream(tempFile)) {
			fos.write(multipartFile.getBytes());
			fos.close();
		}
		return tempFile;
	}

	@Value("${app.upload.dir}")
	private String uploadDir;

	private String uploadFile(File file, String fileName, String type) throws IOException {
	    File destination = new File(uploadDir + File.separator + fileName);
	    Files.copy(file.toPath(), destination.toPath());
	    return destination.getAbsolutePath();
	}
	
	public String upload(MultipartFile multipartFile, String type) {
		try {
			String fileName = multipartFile.getOriginalFilename();
			File file = this.convertToFile(multipartFile, fileName);
			String URL = this.uploadFile(file, fileName, type);
			if (file.delete()) {
				System.out.println("File deleted successfully");
			}
			return fileName;
		} catch (Exception e) {
			e.printStackTrace();
			return "Image couldn't upload, Something went wrong";
		}
	}
}
