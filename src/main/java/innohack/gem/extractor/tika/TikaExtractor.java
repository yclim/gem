package innohack.gem.extractor.tika;


import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.xml.sax.SAXException;

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
                    Metadata metadata = new Metadata();
                    metadata.set(Metadata.RESOURCE_NAME_KEY, result.toString());
                    MediaType mimetype = tika.getDetector().detect(
                            TikaInputStream.get(result), metadata);

                    TikaMimeEnum mimeType = determineMimeTypeAndParser(mimetype, result);
                    System.out.println("result is " + result.toAbsolutePath() + " mimeType is " + mimeType.getMimeType());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TikaMimeEnum determineMimeTypeAndParser (MediaType mediaType, Path path) {
        // need to force find all the type here

        if (mediaType.getSubtype().equals(TikaMimeEnum.PDF.getMimeType())) {
            TikaPdfParser pdfParser = new TikaPdfParser(path);
            try {
                pdfParser.parsePDF();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TikaException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
            return TikaMimeEnum.PDF;

        }else if (mediaType.getSubtype().equals(TikaMimeEnum.MSWORD.getMimeType())) {
            return TikaMimeEnum.MSWORD;

        }else if (mediaType.getSubtype().equals(TikaMimeEnum.MSEXCEL.getMimeType())) {
            return TikaMimeEnum.MSEXCEL;

        }else if (mediaType.getSubtype().equals(TikaMimeEnum.CSV.getMimeType())) {
            TikaTextAndCsvParser csvParser = new TikaTextAndCsvParser(path);
                //this is usng tika
                // csvParser.parseTextAndCsv();
                // this is using opencsv
                csvParser.parseUsingOpenCsv();

            return TikaMimeEnum.CSV;

        }else
            return TikaMimeEnum.UNKNOWN;

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
