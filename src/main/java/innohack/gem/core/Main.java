package innohack.gem.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import innohack.gem.GemApplication;
import innohack.gem.core.GEMMain;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.Project;
import innohack.gem.core.entity.extractor.ExtractedRecords;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {
  public static void main(String arg[]) throws Exception {
    String file;
    String spec;
    if (arg.length > 1) {
      file = arg[0];
      spec = arg[1];
    }
    file = "src/test/resources/reviews.csv";
    spec = "C:/Users/roger/Downloads/test.json";
    ObjectMapper mapper = new ObjectMapper();
    try {
      Project project = mapper.readValue(new File(spec), Project.class);
      //      GEMFile gemFile = new GEMFile(file);
      //      GEMProcessor gemProcessor = new GEMProcessor(gemFile, project);
      //      List<ExtractedRecords> recordsList = gemProcessor.run();

      File f = new File(file);

      List<ExtractedRecords> recordsList = GEMMain.process(f, project);

      for (ExtractedRecords extractedRecords : recordsList) {
        extractedRecords
            .getRecords()
            .forEach(row -> System.out.println(Arrays.toString(row.toArray())));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
