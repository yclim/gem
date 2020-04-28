package innohack.gem.entity.util;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class FileUtilForTesting {

  public static void deleteTestFile(File f) throws IOException {
    if (f.exists()) FileUtils.forceDelete(f);
  }

  public static boolean isWindows(String OS) {

    return (OS.indexOf("win") >= 0);
  }

  public static boolean isMac(String OS) {

    return (OS.indexOf("mac") >= 0);
  }

  public static boolean isUnix(String OS) {

    return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
  }

  public static boolean isSolaris(String OS) {

    return (OS.indexOf("sunos") >= 0);
  }
}
