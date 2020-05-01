package innohack.gem;

import innohack.gem.filegen.GenerateMockFiles;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GemApplication {

  private static final Logger LOGGER = LoggerFactory.getLogger(GemApplication.class);

  private static void setupSampleFiles(String command, String path, String count) {
    if ("RUN_SAMPLES".equals(command)) {
      try {
        File folder = new File(path);
        if (folder.exists() && folder.isDirectory()) {
          FileUtils.deleteDirectory(new File(path));
          LOGGER.info("Removed old sample files");
        }
        GenerateMockFiles.main(new String[] {path, count});
        LOGGER.info("Generated {} sample files in {}", path, count);
      } catch (IOException ex) {
        LOGGER.error("Error in generating sample files", ex);
      }
    }
  }

  /**
   * Starts the app. In cloud mode, this optionally generates sample files
   *
   * @param args To generate sample files, pass the following arguments: arg[0]: RUN_SAMPLES arg[1]:
   *     <path of samples> arg[2]: <number of samples to generate>
   */
  public static void main(String[] args) {
    if (args.length >= 3) {
      setupSampleFiles(args[0], args[1], args[2]); // command, path, count
    }

    SpringApplication.run(GemApplication.class, args);
  }
}
