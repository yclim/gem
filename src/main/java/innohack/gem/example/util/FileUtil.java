package innohack.gem.example.util;

import java.io.*;
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

  public static String fileToString(File file) {
    String fileAsString = null;
    InputStream is = null;
    try {
      is = new FileInputStream(file);
      BufferedReader buf = new BufferedReader(new InputStreamReader(is));
      String line = buf.readLine();
      StringBuilder sb = new StringBuilder();
      while (line != null) {
        sb.append(line).append("\n");
        line = buf.readLine();
      }
      fileAsString = sb.toString();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return fileAsString;
  }
}
