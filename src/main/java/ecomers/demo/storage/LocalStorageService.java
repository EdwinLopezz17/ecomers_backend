package ecomers.demo.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService{

    @Value("${storage.location:uploads}")
    private String storageLocation;

    @Override
    public String save(MultipartFile file) {
        try{
            Path uploadPath = Paths.get(storageLocation);
            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }
            String extension = getExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + "."+extension;
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Error saving file"+e.getMessage());
        }
    }

    @Override
    public void delete(String filename) {
        try{
            Path filePath = Paths.get(storageLocation).resolve(filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e){
            throw new RuntimeException("Error deleting file" + e.getMessage());
        }
    }

    private String getExtension(String filename){
        if(filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
