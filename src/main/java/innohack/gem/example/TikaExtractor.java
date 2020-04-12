package innohack.gem.example;

import innohack.gem.extractor.tika.*;

public class TikaExtractor {

  public static void main(String[] args) {

    System.out.println("running tika");
    // please code here for the path to perform the testing
    String pathFolder = "C://Users//duo_t//Documents//WFH//data";
    try {
      TikaUtil tikaUtil = new TikaUtil();
      tikaUtil.walkPath(pathFolder);
    } catch (Exception ex) {
      System.out.println("error ex: " + ex.toString());
    }
  }
}
