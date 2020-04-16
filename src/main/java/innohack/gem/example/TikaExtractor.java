package innohack.gem.example;

import innohack.gem.entity.GEMFile;
import innohack.gem.example.util.FileUtil;
import innohack.gem.example.tika.TikaUtil;
import java.nio.file.Path;
import java.util.List;

public class TikaExtractor {

  public static void main(String[] args) {

    System.out.println("running tika");
    // please code here for the path to perform the testing
    String pathFolder = "C://Users//duo_t//Documents//WFH//data";
    try {

      TikaUtil tikaUtil = new TikaUtil();
      List<Path> results = FileUtil.walkPath(pathFolder);

      for (Path result : results) {
        System.out.println("each result is " + result.toAbsolutePath());

        GEMFile gFile = new GEMFile(result);
        gFile.extract();
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
