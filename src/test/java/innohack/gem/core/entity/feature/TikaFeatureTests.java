package innohack.gem.core.entity.feature;

import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.core.GEMMain;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.feature.AbstractFeature;
import innohack.gem.core.feature.TikaFeature;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TikaFeatureTests {

  @Test
  void testTikaMetadataParser() throws Exception {
    String path = "src/test/resources";
    String filename = "customer_0.csv";

    File file = new File(path + "/" + filename);
    GEMFile gFile = GEMMain.extractFeature(file);
    Collection<AbstractFeature> abstractFeatureC = gFile.getData();

    Iterator<AbstractFeature> iterator = abstractFeatureC.iterator();

    // contains both tika and csv feature
    assertTrue(abstractFeatureC.size() == 2);
    while (iterator.hasNext()) {
      AbstractFeature abs = iterator.next();
      if (abs.getClass().getName().equals(TikaFeature.class.getName())) {
        testMetadata((TikaFeature) abs);
      }
    }
  }

  private void testMetadata(TikaFeature abs) {
    TikaFeature tikaFeature = (TikaFeature) abs;

    Map<String, String> metaData = tikaFeature.getMetadata();
    int metaDataCount = 0;
    if (metaData != null) {
      for (Map.Entry<String, String> entry : metaData.entrySet()) {

        if (metaDataCount == 0) {
          assertTrue(entry.getKey().equals("X-Parsed-By"));
          assertTrue(entry.getValue().equals("org.apache.tika.parser.DefaultParser"));
        } else if (metaDataCount == 1) {
          assertTrue(entry.getKey().equals("Content-Encoding"));
        } else if (metaDataCount == 2) {
          assertTrue(entry.getKey().equals("csv:delimiter"));
          assertTrue(entry.getValue().equals("comma"));
        } else if (metaDataCount == 3) {
          assertTrue(entry.getKey().equals("Content-Type"));
        }
        metaDataCount++;
      }
    }
  }
}
