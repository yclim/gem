package innohack.gem.example;

import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.feature.AbstractFeature;
import innohack.gem.example.tika.TikaUtil;
import innohack.gem.example.util.FileUtil;
import java.nio.file.Path;
import java.util.Iterator;
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

        GEMFile gFile = new GEMFile(result.getFileName().toString(), result.getParent().toString());
        gFile.extract();
        List<AbstractFeature> abstractFeatureC = gFile.getData();

        Iterator<AbstractFeature> iterator = abstractFeatureC.iterator();
        while (iterator.hasNext()) {
          AbstractFeature abs = iterator.next();
          if (abs.getMetadata() != null) {
            abs.printMetadata();
          }
        }
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
