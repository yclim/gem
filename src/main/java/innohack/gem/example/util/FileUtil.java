package innohack.gem.example.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtil {

  /**
   * Walkpath to walk though a folder contains different files
   *
   * @param path of the folder which contains the files
   */
  public static List<Path> walkPath(String path) {

    List<Path> results = new ArrayList<>();

    try (Stream<Path> walk = Files.walk(Paths.get(path))) {

      results =
          walk.filter(Files::isRegularFile)
              .map(x -> x.toAbsolutePath())
              .collect(Collectors.toList());

    } catch (IOException e) {
      e.printStackTrace();

    } finally {
      return results;
    }
  }
}
