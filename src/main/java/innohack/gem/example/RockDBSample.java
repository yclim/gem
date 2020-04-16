package innohack.gem.example;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import innohack.gem.entity.gem.data.CsvFeature;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.rocksdb.*;

public class RockDBSample {

  static {
    RocksDB.loadLibrary();
  }

  public static void main(final String[] args) {
    String dbPath = args[0];
    String filePath = args[1];
    // insert record
    putCsvFeature(dbPath, filePath);
    // get keys
    List<String> list = scanPrefixKey(dbPath, "csv");
    // get values
    getBatch(dbPath, list);
  }

  // return list of csv features
  public static List<CsvFeature> getBatch(String dbPath, List<String> keys) {

    List<CsvFeature> list = new ArrayList();
    List<byte[]> lookupKeys = new ArrayList<>();

    for (String key : keys) {
      lookupKeys.add(key.getBytes());
    }

    try (final Options options = new Options().setCreateIfMissing(true)) {

      // a factory method that returns a RocksDB instance
      try (final RocksDB db = RocksDB.open(options, dbPath)) {

        List<byte[]> results = db.multiGetAsList(lookupKeys);
        for (byte[] result : results) {
          CsvFeature temp = deserialize(result);
          System.out.println("total row: " + temp.getTotalRow());
          list.add(temp);
        }

      }

    } catch (Exception e) {
      // do some error handling
      e.printStackTrace();
    }

    return list;
  }

  // how we set the key name convention will affect the scan performance of db
  public static List<String> scanPrefixKey(String dbPath, String prefixStr) {
    // the Options class contains a set of configurable DB options
    // that determines the behaviour of the database.

    List<String> list = new ArrayList();

    try (final Options options = new Options().setCreateIfMissing(true)) {

      // a factory method that returns a RocksDB instance
      try (final RocksDB db = RocksDB.open(options, dbPath)) {

        try {
          final byte[] prefix = prefixStr.getBytes();

          String prefixString = new String(prefix);

         /*
            An iterator that specifies a prefix (via ReadOptions) will use these bloom bits
            to avoid looking into data files that do not contain keys with the specified key-prefix.
          */

          final RocksIterator iterator =
              db.newIterator(new ReadOptions().setPrefixSameAsStart(true));

          for (iterator.seek(prefix); iterator.isValid(); iterator.next()) {

            final String key = new String(iterator.key());

            if (key.startsWith(prefixString)) {
                list.add(key);
                System.out.println("key: " + key);
            } else {
                /* To check
                Since next() can go across the boundary to a different prefix,
                you will need to check the end condition:
                break out of loop if prefix not matched
                */
                break;
            }

          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } catch (Exception e) {
      // do some error handling
      e.printStackTrace();
    }
    return list;
  }

  public static void putCsvFeature(String dbPath, String filePath) {
    // the Options class contains a set of configurable DB options
    // that determines the behaviour of the database.
    try (final Options options = new Options().setCreateIfMissing(true)) {

      // a factory method that returns a RocksDB instance
      try (final RocksDB db = RocksDB.open(options, dbPath)) {

        // set filename as key for this record
        File file = new File(filePath);

        if(file.exists()) {
          byte[] fileKey = file.getName().getBytes();

          // set csv feature
          CsvFeature csvFeature = new CsvFeature();
          csvFeature.extract(new File(filePath));

          // convert object to byte array
          byte[] value = serialize(csvFeature);

          // the key already exists in the database, the previous value will be overwritten.
          // need a unique key
          // Keys and values are byte arrays.
          System.out.println("Put fileKey" + file.getName());

          db.put(fileKey, value);

          // convert byte array back to csv feature
          CsvFeature testFeature = deserialize(db.get(fileKey));

          System.out.println("check assertions");
          // assert csv feature and test feature
          assert csvFeature.getTotalRow() == testFeature.getTotalRow() : "total row not matched";
          assert csvFeature.getHeader() == testFeature.getHeader() : "header not matched";
          assert csvFeature.getContents() == testFeature.getContents() : "content not matched";
          assert csvFeature.getColRecords() == testFeature.getColRecords()
                  : "col records not matched";
        } else {
          System.out.println("File not found: " + filePath);
        }

      }

    } catch (Exception e) {
      // do some error handling
      e.printStackTrace();
    }
  }

  private static byte[] serialize(Object obj) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mapper.writeValue(os, obj);

    return os.toByteArray();
  }

  private static CsvFeature deserialize(byte[] bytes) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    final ObjectReader r = mapper.reader(CsvFeature.class);
    // enable one feature, disable another
    CsvFeature value =
        r.with(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .without(DeserializationFeature.WRAP_EXCEPTIONS)
            .readValue(bytes);
    return value;
  }
}
