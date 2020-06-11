package innohack.gem.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.Project;
import innohack.gem.core.entity.extractor.ExtractedRecords;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
  public static void main(String arg[]) {
    String file;
    String spec;
    if (arg.length > 1) {
      file = arg[0];
      spec = arg[1];
    }
    file = "/home/ysj/Desktop/GEM/gem1/gem/src/test/resources/reviews.csv";
    spec = "/home/ysj/Downloads/export (10).json";
    ObjectMapper mapper = new ObjectMapper();
    try {
      Project project = mapper.readValue(new File(spec), Project.class);
      GEMFile gemFile = new GEMFile(file);
      GEMProcessor gemProcessor = new GEMProcessor(gemFile, project);
      List<ExtractedRecords> recordsList = gemProcessor.run();
      System.out.print(recordsList);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
