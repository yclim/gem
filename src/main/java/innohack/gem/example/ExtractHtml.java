package innohack.gem.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;

public class ExtractHtml {

    public static void main(String[] args) throws Exception{

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
            System.out.println("(i) Print Title and Body");
            System.out.println(doc.title());
            System.out.println(doc.body().text());
            System.out.println();

            System.out.println("(ii) Print Meta Tags");
            // print meta tag -author name, org name etc
            Elements metaTags = doc.getElementsByTag("meta");
            printMetaTagFromHtml(metaTags);
            System.out.println();

            // print all links
            System.out.println("(iii) Print Links from Html");
            printLinksFromHtml(doc);
            System.out.println();

            // print table data - like contact list table from html?
            System.out.println("(iv) Print Tables from Html");
            printTableFromHtml(doc);
            System.out.println();

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
                print(" * %s: <%s> %sx%s (%s)",
                        src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
                        trim(src.attr("alt"), 20));
            else
                print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
        }

        print("\nImports: (%d)", imports.size());
        for (Element link : imports) {
            print(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel"));
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
                System.out.println(String.join(",", row.select("td,th").eachText()));
            }
        }
    }

    private static void printMetaTagFromHtml(Elements metaTags) {
        // extract meta tag
        for (Element metaTag : metaTags) {
            String content = metaTag.attr("content");
            String name = metaTag.attr("name");
            if (!content.equals("") || !name.equals(""))
                print("name: %s, content: %s", name, content);
            // String charset = metaTag.attr("charset");
            // String httpEquiv = metaTag.attr("http-equiv");
            // print("content: %s name: %s charset: %s httpEquv: %s", content, name, charset, httpEquiv);
        }
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }

}
