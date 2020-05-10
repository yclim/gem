package innohack.gem.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtractHtml {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExtractHtml.class);

  public static void main(String[] args) throws Exception {

    try {
      // extract by file
      // String filePath = args[0];
      // File input = new File(filePath);
      // Document doc = Jsoup.parse(input, "UTF-8");

      // by website
      String url = "https://www.w3schools.com/html/html_tables.asp";
      Document doc = Jsoup.connect(url).get();

      // Not sure if we can get html file
      // Possible info to extract from html

      // print title and body
      LOGGER.debug("(i) Print Title and Body");
      LOGGER.debug(doc.title());
      LOGGER.debug(doc.body().text());

      LOGGER.debug("(ii) Print Meta Tags");
      // print meta tag -author name, org name etc
      Elements metaTags = doc.getElementsByTag("meta");
      printMetaTagFromHtml(metaTags);

      // print all links
      LOGGER.debug("(iii) Print Links from Html");
      printLinksFromHtml(doc);

      // print table data - like contact list table from html?
      LOGGER.debug("(iv) Print Tables from Html");
      printTableFromHtml(doc);

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private static void printLinksFromHtml(Document doc) {
    Elements links = doc.select("a[href]");
    Elements media = doc.select("[src]");
    Elements imports = doc.select("link[href]");

    print("\nMedia: (%d)", media.size());
    for (Element src : media) {
      if (src.normalName().equals("img"))
        print(
            " * %s: <%s> %sx%s (%s)",
            src.tagName(),
            src.attr("abs:src"),
            src.attr("width"),
            src.attr("height"),
            trim(src.attr("alt"), 20));
      else print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
    }

    print("\nImports: (%d)", imports.size());
    for (Element link : imports) {
      print(" * %s <%s> (%s)", link.tagName(), link.attr("abs:href"), link.attr("rel"));
    }

    print("\nLinks: (%d)", links.size());
    for (Element link : links) {
      print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
    }
  }

  private static void printTableFromHtml(Document doc) {
    // extract table data
    for (Element table : doc.select("table")) {
      System.out.println("");
      for (Element row : table.select("tr")) {
        LOGGER.debug(String.join(",", row.select("td,th").eachText()));
      }
    }
  }

  private static void printMetaTagFromHtml(Elements metaTags) {
    // extract meta tag
    for (Element metaTag : metaTags) {
      String content = metaTag.attr("content");
      String name = metaTag.attr("name");
      if (!content.equals("") || !name.equals("")) print("name: %s, content: %s", name, content);
      // String charset = metaTag.attr("charset");
      // String httpEquiv = metaTag.attr("http-equiv");
      // print("content: %s name: %s charset: %s httpEquv: %s", content, name, charset, httpEquiv);
    }
  }

  private static void print(String msg, Object... args) {
    LOGGER.debug(String.format(msg, args));
  }

  private static String trim(String s, int width) {
    if (s.length() > width) return s.substring(0, width - 1) + ".";
    else return s;
  }
}
