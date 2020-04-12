package innohack.gem.example;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExtractBasicFileInfo {

    public static void main(String[] args) {

        Path path = Paths.get(args[0]);
        String name = args[1];
        String originalFileName = args[2];
        String contentType = args[3];
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        MultipartFile result = new MockMultipartFile(name,
                originalFileName, contentType, content);

        System.out.println("original name: " + result.getOriginalFilename());
        System.out.println("name:" + result.getName());
        System.out.println("content type: " + result.getContentType());
        System.out.println("size: " + result.getSize());

    }

}
