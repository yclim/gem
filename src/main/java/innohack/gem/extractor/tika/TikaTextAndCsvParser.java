package innohack.gem.extractor.tika;

import com.fasterxml.jackson.core.util.Separators;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.csv.TextAndCSVParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import com.opencsv.*;
import au.com.bytecode.opencsv.CSVReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TikaTextAndCsvParser {

    private Path filePath;

    public TikaTextAndCsvParser(Path filePath) {
        System.out.println("filepath is " + filePath);
        this.filePath = filePath;
    }

    public void parseTextAndCsv() throws IOException,SAXException, TikaException {
//detecting the file type
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(new File(String.valueOf(this.filePath.toAbsolutePath())));
        ParseContext pcontext = new ParseContext();

        //Text document parser
        TextAndCSVParser   csvParser = new TextAndCSVParser ();
        csvParser.parse(inputstream, handler, metadata,pcontext);

        System.out.println("Contents of the document:" + handler.toString());
        System.out.println("Metadata of the document:");
        String[] metadataNames = metadata.names();

        for(String name : metadataNames) {
            System.out.println(name + " : " + metadata.get(name));
        }
    }

    private char detectSeparators(BufferedReader pBuffered) throws IOException {
        int lMaxValue = 0;
        char lCharMax = ',';
        pBuffered.mark(2048);

        ArrayList<Separators> lSeparators = new ArrayList<Separators>();
        lSeparators.add(new Separators(','));
        lSeparators.add(new Separators(';'));
        lSeparators.add(new Separators('\t'));

        Iterator<Separators> lIterator = lSeparators.iterator();
        while (lIterator.hasNext()) {
            Separators lSeparator = lIterator.next();
            au.com.bytecode.opencsv.CSVReader lReader = new CSVReader(pBuffered, lSeparator.getSeparator());
            String[] lLine;
            lLine = lReader.readNext();
            lSeparator.setCount(lLine.length);

            if (lSeparator.getCount() > lMaxValue) {
                lMaxValue = lSeparator.getCount();
                lCharMax = lSeparator.getSeparator();
            }
            pBuffered.reset();
        }
        System.out.println("The seperator is " + lCharMax);
        return lCharMax;
    }


    public void parseUsingOpenCsv() {

        Reader reader = null;
        try {
            reader = Files.newBufferedReader(this.filePath, UTF_8);

        System.out.println(" Using opencsv ");
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(detectSeparators((BufferedReader)reader))
                .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_QUOTES)
                .withIgnoreLeadingWhiteSpace(true)
                .withIgnoreQuotations(false)
                .withStrictQuotes(false)
                .build();

        com.opencsv.CSVReader csvReader = new CSVReaderBuilder(reader)
                .withCSVParser(parser)
                .build();

            // read all records at once
        List<String[]> records = csvReader.readAll();

        // iterate through list of records
        for (String[] record : records) {
            for (String cell : record) {
                System.out.print(cell + " ");

            }
            System.out.println("\n");
        }

        // close readers
        csvReader.close();
        reader.close();

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    private class Separators {
        private char fSeparatorChar;
        private int  fFieldCount;

        public Separators(char pSeparator) {
            fSeparatorChar = pSeparator;
        }
        public void setSeparator(char pSeparator) {
            fSeparatorChar = pSeparator;
        }

        public void setCount(int pCount) {
            fFieldCount = pCount;
        }
        public char getSeparator() {
            return fSeparatorChar;
        }
        public int getCount() {
            return fFieldCount;
        }
    }

}
