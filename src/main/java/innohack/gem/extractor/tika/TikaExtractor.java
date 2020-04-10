package innohack.gem.extractor.tika;


import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TikaExtractor {

    private TikaConfig tika;

    private static final String PDF = "pdf";
    private static final String MSEXCEL = "vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String MSWORD = "vnd.openxmlformats-officedocument.wordprocessingml.document";

    public TikaExtractor() {
        try {
            tika = new TikaConfig();

        }catch (IOException | TikaException ex) {
                System.out.println("error ex: " + ex.toString());

        }
    }


    public static void main(String[] args) {

        System.out.println("running tika");
        // please code here for the path to perform the testing
        String pathFolder = "C://Users//duo_t//Documents//WFH//data";
        try {
            TikaExtractor extract = new TikaExtractor();
            extract.walkPath(pathFolder);
        }catch (IOException  ex) {
            System.out.println("error ex: " + ex.toString());

        }

    }

    public  void walkPath (String path) throws IOException {
        try (Stream<Path> walk = Files.walk(Paths.get(path))) {

            List<Path> results = walk.filter(Files::isRegularFile)
                    .map(x -> x.toAbsolutePath()).collect(Collectors.toList());

            for (Path result : results) {
                System.out.println("each result is " + result.toAbsolutePath());
                try (InputStream inputStream = Files.newInputStream(result.toAbsolutePath())) {

                    Metadata metadata = new Metadata();
                    metadata.set(Metadata.RESOURCE_NAME_KEY, result.toString());
                    MediaType mimetype = tika.getDetector().detect(
                            TikaInputStream.get(result), metadata);

                    determineMimeType(mimetype);

                    /*
                    String docType = detectDocTypeUsingFacade(inputStream);
                    System.out.println("filename is " + result.toString() + " doctype is : " +
                            docType + " mineType is 1. " + mimetype.getSubtype() + "2. " + mimetype.getBaseType()
                            + "3. " + mimetype.toString());

                     */
                } catch (IOException io) {
                    System.out.println(io.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void determineMimeType (MediaType mediaType) {
        // need to force find all the type here
        switch (mediaType.getSubtype()) {
            case PDF: System.out.println("This is PDF");
                break;
            case MSEXCEL: System.out.println("This is MS EXCEL");
                break;
            case MSWORD: System.out.println("This is MS WORD");
                break;
            default: System.out.println("UNKNOWN");

        }
    }

    public String detectDocTypeUsingDetector(InputStream stream)
            throws IOException {
        Detector detector = new DefaultDetector();
        Metadata metadata = new Metadata();

        MediaType mediaType = detector.detect(stream, metadata);

        return mediaType.toString();
    }

    public String detectDocTypeUsingFacade(InputStream stream)
            throws IOException {

        Tika tika = new Tika();
        String mediaType = tika.detect(stream);
        return mediaType;
    }

}
