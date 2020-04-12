package innohack.gem.filegen;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GenerateMockFiles {

    final static Path defaultDest = Paths.get("target/samples/");

    public static void main(String[] args) throws IOException {
        Path dest = defaultDest;
        if (args.length == 1) {
            dest = Paths.get(args[0]);
        }
        PdfFileGenerator.generateProductsPdfFiles(100, dest);
    }
}
