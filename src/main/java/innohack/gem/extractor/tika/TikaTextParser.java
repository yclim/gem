package innohack.gem.extractor.tika;

import org.apache.tika.parser.txt.TXTParser;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TikaTextParser {

    public static void parseText(final String[] args) throws IOException,SAXException, TikaException {
//detecting the file type
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(new File("example.txt"));
        ParseContext pcontext = new ParseContext();

        //Text document parser
        TXTParser  textParser = new TXTParser();
        textParser.parse(inputstream, handler, metadata,pcontext);
        System.out.println("Contents of the document:" + handler.toString());
        System.out.println("Metadata of the document:");
        String[] metadataNames = metadata.names();

        for(String name : metadataNames) {
            System.out.println(name + " : " + metadata.get(name));
        }
    }

}
