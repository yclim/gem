package innohack.gem.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class AppProperties {

    public static final String PROP_SYNC_DIRECTORY = "sync.directory";
    private static final String PROPERTIES_PATH =
            AppProperties.class.getClassLoader().getResource("application.properties").getPath();

    public static String get(String key) {
        Properties prop = new Properties();
        try {
            FileReader reader = new FileReader(PROPERTIES_PATH);
            prop.load(reader);
            return prop.getProperty(key);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void put(String key, String value) {
        Properties prop = new Properties();
        try {
            FileReader reader = new FileReader(PROPERTIES_PATH);
            prop.load(reader);
            prop.setProperty(key, value);
            FileWriter writer = new FileWriter(PROPERTIES_PATH);
            prop.store(writer, "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
