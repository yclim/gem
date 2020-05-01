package innohack.gem.dao;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.rocksdb.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RocksDatabase<K, V> {

    private static final String DB_PATH = "./GemDB/";

    static {
        RocksDB.loadLibrary();
    }

    private String dbName;
    private Class keyType;
    private Class valueType;

    public RocksDatabase(String dbName, Class keyType, Class valueType) {
        this.dbName = dbName;
        createFolderPath(DB_PATH);
        this.keyType = keyType;
        this.valueType = valueType;
    }

    private static Object deserialize(Class type, byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC);
        final ObjectReader r = mapper.readerFor(type);
        // enable one feature, disable another
        Object value =
                r.with(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                        .without(DeserializationFeature.WRAP_EXCEPTIONS)
                        .readValue(bytes);
        return value;
    }

    private void createFolderPath(String dbPath) {
        new File(dbPath).mkdirs();
    }

    public boolean put(K key, V value) {
        // the Options class contains a set of configurable DB options
        // that determines the behaviour of the database.
        try (final Options options = new Options().setCreateIfMissing(true)) {

            // a factory method that returns a RocksDB instance
            try (final RocksDB db = RocksDB.open(options, new File(DB_PATH, dbName).getAbsolutePath())) {
                byte[] key_byte = serialize(key);
                byte[] value_byte = serialize(value);
                System.out.println("Put " + key);
                db.put(key_byte, value_byte);
                return true;
            }

        } catch (Exception e) {
            // do some error handling
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(K key) {
        try (final Options options = new Options().setCreateIfMissing(true)) {
            try (final RocksDB db = RocksDB.open(options, new File(DB_PATH, dbName).getAbsolutePath())) {
                byte[] key_byte = serialize(key);
                System.out.println("Delete " + key);
                db.delete(key_byte);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAll() {
        try (final Options options = new Options().setCreateIfMissing(true)) {
            try (final RocksDB db = RocksDB.open(options, new File(DB_PATH, dbName).getAbsolutePath())) {
                RocksIterator itr = db.newIterator();
                itr.seekToFirst();
                byte[] start = itr.key();
                itr.seekToLast();
                byte[] end = itr.key();
                db.deleteRange(new WriteOptions(), start, end);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            new File(DB_PATH, dbName).delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public V get(K key) {
        // the Options class contains a set of configurable DB options
        // that determines the behaviour of the database.
        try (final Options options = new Options().setCreateIfMissing(true)) {

            // a factory method that returns a RocksDB instance
            try (final RocksDB db = RocksDB.open(options, new File(DB_PATH, dbName).getAbsolutePath())) {
                byte[] key_byte = serialize(key);
                byte[] value = db.get(key_byte);
                return (V) deserialize(valueType, value);
            }

        } catch (Exception e) {
            // do some error handling
            e.printStackTrace();
        }
        return null;
    }

    public Iterator<K, V> iterator() {
        try (final Options options = new Options().setCreateIfMissing(true)) {
            try (final RocksDB db = RocksDB.open(options, new File(DB_PATH, dbName).getAbsolutePath())) {
                return new Iterator<K, V>(db.newIterator(new ReadOptions().setPrefixSameAsStart(true)));
            }

        } catch (Exception e) {
            // do some error handling
            e.printStackTrace();
        }
        return null;
    }

    public List<K> getKeys() {
        List<K> list = new ArrayList();

        try (final Options options = new Options().setCreateIfMissing(true)) {
            // a factory method that returns a RocksDB instance
            try (final RocksDB db = RocksDB.open(options, new File(DB_PATH, dbName).getAbsolutePath())) {
                try {
                    final RocksIterator iterator = db.newIterator(new ReadOptions());

                    for (iterator.seekToLast(); iterator.isValid(); iterator.prev()) {
                        final K key = (K) deserialize(keyType, iterator.key());
                        list.add(key);
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

    public HashMap<K, V> getKeyValues() {
        HashMap<K, V> hashMap = new HashMap();

        try (final Options options = new Options().setCreateIfMissing(true)) {
            // a factory method that returns a RocksDB instance
            try (final RocksDB db = RocksDB.open(options, new File(DB_PATH, dbName).getAbsolutePath())) {
                try {
                    final RocksIterator iterator = db.newIterator(new ReadOptions());

                    for (iterator.seekToLast(); iterator.isValid(); iterator.prev()) {
                        final K key = (K) deserialize(keyType, iterator.key());
                        final V value = (V) deserialize(valueType, iterator.value());
                        hashMap.put(key, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            // do some error handling
            e.printStackTrace();
        }
        return hashMap;
    }

    // how we set the key name convention will affect the scan performance of db
    public List<K> scanPrefixKey(K prefixStr) {
        // the Options class contains a set of configurable DB options
        // that determines the behaviour of the database.

        List<K> list = new ArrayList();

        try (final Options options = new Options().setCreateIfMissing(true)) {

            // a factory method that returns a RocksDB instance
            try (final RocksDB db = RocksDB.open(options, new File(DB_PATH, dbName).getAbsolutePath())) {

                try {
                    final byte[] prefixByte = serialize(prefixStr);
                    // prefixStr = (K)deserialize(keyType,prefixByte);
                    // String prefixString = new String(prefix);

          /*
            An iterator that specifies a prefix (via ReadOptions) will use these bloom bits
            to avoid looking into data files that do not contain keys with the specified key-prefix.
          */

                    final RocksIterator iterator =
                            db.newIterator(new ReadOptions().setPrefixSameAsStart(true));

                    for (iterator.seek(prefixByte); iterator.isValid(); iterator.next()) {

                        final K key = (K) deserialize(keyType, iterator.key());

                        if (keyType == String.class) {
                            if (((String) key).startsWith((String) prefixStr)) {
                                list.add(key);
                            } else {
                /* To check
                Since next() can go across the boundary to a different prefix,
                you will need to check the end condition:
                break out of loop if prefix not matched
                */
                                break;
                            }
                        } else {
                            if (key.equals(prefixStr)) {
                                list.add(key);
                                break;
                            } else {
                                break;
                            }
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

    private byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC);
        mapper.writeValue(os, obj);

        return os.toByteArray();
    }

    public class KeyValue<K, V> {
        public K key;
        public V value;

        public KeyValue(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public class Iterator<K, V> {
        RocksIterator iterator;

        protected Iterator(RocksIterator iterator) {
            this.iterator = iterator;
            this.iterator.seekToFirst();
        }

        public KeyValue keyValue() {
            return new KeyValue(getKeyOrValue(keyType), getKeyOrValue(valueType));
        }

        public K key() {
            return (K) getKeyOrValue(keyType);
        }

        public V value() {
            return (V) getKeyOrValue(valueType);
        }

        private Object getKeyOrValue(Class<?> _class) {
            try {
                byte[] content;
                if (_class == keyType) {
                    content = iterator.key();
                } else {
                    content = iterator.value();
                }
                if (content != null) {
                    return deserialize(_class, content);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void next() {
            iterator.next();
        }

        public boolean isvalid() {
            return iterator.isValid();
        }
    }
}
