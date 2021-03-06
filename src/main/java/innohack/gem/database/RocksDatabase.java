package innohack.gem.database;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RocksDatabase<K, V> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RocksDatabase.class);
  private static final String DB_PATH = "./target/GemDB/";

  /*
  Use to ensure singleton instance for each database file
   */
  private static ConcurrentHashMap<String, RocksDatabase> instancesMap = new ConcurrentHashMap<>();

  static {
    RocksDB.loadLibrary();
  }

  // each instance will have a pair of readwrite lock to manage concurrent access
  private ReadWriteLock dbLock;
  private RocksDB db;
  private String dbName;
  private Class<K> keyType;
  private Class<V> valueType;

  private RocksDatabase(String dbName, Class<K> keyType, Class<V> valueType) {
    this.dbName = dbName;
    createFolderPath(DB_PATH);
    this.keyType = keyType;
    this.valueType = valueType;
    this.dbLock = new ReentrantReadWriteLock();
  }

  public static <K, V> RocksDatabase<K, V> getInstance(
      String dbName, Class<K> keyType, Class<V> valueType) {
    RocksDatabase<K, V> db = instancesMap.get(dbName);
    if (db != null) {
      return db;
    } else {
      db = new RocksDatabase<K, V>(dbName, keyType, valueType);
      instancesMap.put(dbName, db);
      return db;
    }
  }

  private void createFolderPath(String dbPath) {
    new File(dbPath).mkdirs();
  }

  public boolean put(K key, V value) {
    boolean result = false;
    dbLock.writeLock().lock();
    File dbFile = new File(DB_PATH, dbName);
    // the Options class contains a set of configurable DB options
    // that determines the behaviour of the database.
    try (final Options options = new Options().setCreateIfMissing(true)) {
      // a factory method that returns a RocksDB instance
      try (final RocksDB db = RocksDB.open(options, dbFile.getAbsolutePath())) {
        byte[] key_byte = serialize(key);
        byte[] value_byte = serialize(value);
        LOGGER.debug(dbName + ": " + "put " + key + ", " + value);
        db.put(key_byte, value_byte);
        db.close();
        result = true;
      }

    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    } finally {
      dbLock.writeLock().unlock();
    }
    return result;
  }

  public boolean putHashMap(Map<K, V> map) {
    return putHashMap(map, false);
  }

  public boolean putHashMap(Map<K, V> map, boolean dropPrevious) {
    boolean result = false;
    dbLock.writeLock().lock();
    File dbFile = new File(DB_PATH, dbName);
    // the Options class contains a set of configurable DB options
    // that determines the behaviour of the database.
    try (final Options options = new Options().setCreateIfMissing(true)) {
      // a factory method that returns a RocksDB instance
      try (final RocksDB db = RocksDB.open(options, dbFile.getAbsolutePath())) {
        if (dropPrevious) {
          RocksIterator itr = db.newIterator(new ReadOptions());
          itr.seekToFirst();
          byte[] start = itr.key();
          itr.seekToLast();
          byte[] end = itr.key();
          db.deleteRange(new WriteOptions(), start, end);
          db.delete(end);
          itr.close();
          LOGGER.debug(dbName + ": " + "deleteAll");
        }

        for (K key : map.keySet()) {
          V value = map.get(key);
          byte[] key_byte = serialize(key);
          byte[] value_byte = serialize(value);
          LOGGER.debug(dbName + ": " + "put " + key + ", " + value);
          db.put(key_byte, value_byte);
        }
        db.close();
        result = true;
      }

    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    } finally {
      dbLock.writeLock().unlock();
    }
    return result;
  }

  public boolean delete(K key) {
    boolean result = false;
    dbLock.writeLock().lock();
    File dbFile = new File(DB_PATH, dbName);
    try (final Options options = new Options().setCreateIfMissing(true)) {
      try (final RocksDB db = RocksDB.open(options, dbFile.getAbsolutePath())) {
        byte[] key_byte = serialize(key);
        LOGGER.debug(dbName + ": " + "delete " + key);
        db.delete(key_byte);
        db.close();
        result = true;
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    } finally {
      dbLock.writeLock().unlock();
    }
    return result;
  }

  public boolean deleteAll() {
    boolean result = false;
    dbLock.writeLock().lock();
    File dbFile = new File(DB_PATH, dbName);
    try (final Options options = new Options().setCreateIfMissing(true)) {
      try (final RocksDB db = RocksDB.open(options, dbFile.getAbsolutePath())) {
        RocksIterator itr = db.newIterator(new ReadOptions());
        itr.seekToFirst();
        byte[] start = itr.key();
        itr.seekToLast();
        byte[] end = itr.key();
        db.deleteRange(new WriteOptions(), start, end);
        db.delete(end);
        itr.close();
        db.close();
        LOGGER.debug(dbName + ": " + "deleteAll");
        result = true;
      }
    } catch (Exception e) {
    } finally {
      dbLock.writeLock().unlock();
    }
    return result;
  }

  public boolean delete(Collection<K> keys) {
    boolean result = false;
    dbLock.writeLock().lock();
    File dbFile = new File(DB_PATH, dbName);
    try (final Options options = new Options().setCreateIfMissing(true)) {
      try (final RocksDB db = RocksDB.open(options, dbFile.getAbsolutePath())) {
        for (K key : keys) {
          byte[] key_byte = serialize(key);
          db.delete(key_byte);
        }
        db.close();
        LOGGER.debug(dbName + ": " + "delete list " + keys);
        result = true;
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    } finally {
      dbLock.writeLock().unlock();
    }
    return result;
  }

  public V get(K key) {
    V result = null;
    // the Options class contains a set of configurable DB options
    // that determines the behaviour of the database.
    dbLock.readLock().lock();
    try (final Options options = new Options().setCreateIfMissing(true)) {

      File dbFile = new File(DB_PATH, dbName);
      // a factory method that returns a RocksDB instance
      try (final RocksDB db = RocksDB.openReadOnly(options, dbFile.getAbsolutePath())) {
        byte[] key_byte = serialize(key);
        byte[] value = db.get(key_byte);
        db.close();
        LOGGER.debug(dbName + ": " + "get " + key);
        result = (V) deserialize(valueType, value);
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    } finally {
      dbLock.readLock().unlock();
    }
    return result;
  }

  public List<Object> getKeysOrValue(Class<?> type) {
    List<Object> list = new ArrayList<Object>();
    dbLock.readLock().lock();
    try (final Options options = new Options(); ) {
      options.setCreateIfMissing(true);
      // a factory method that returns a RocksDB instance
      File dbFile = new File(DB_PATH, dbName);
      try (final RocksDB db = RocksDB.openReadOnly(options, dbFile.getAbsolutePath())) {
        try (final RocksIterator iterator = db.newIterator(new ReadOptions())) {
          for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
            if (type == keyType) {
              final K key = (K) deserialize(keyType, iterator.key());
              list.add(key);
            } else {
              final V val = (V) deserialize(valueType, iterator.value());
              list.add(val);
            }
          }
          iterator.close();
          db.close();
        } catch (IOException e) {
          LOGGER.error(e.getMessage());
        }
      } catch (RocksDBException e) {
        LOGGER.error(e.getMessage());
      }
    } finally {
      dbLock.readLock().unlock();
    }
    return list;
  }

  public List<K> getKeys() {
    LOGGER.debug(dbName + ": " + "getKeys ");
    return (List<K>) getKeysOrValue(keyType);
  }

  public List<V> getValues() {
    LOGGER.debug(dbName + ": " + "getValues ");
    return (List<V>) getKeysOrValue(valueType);
  }

  public ConcurrentHashMap<K, V> getKeyValues() {
    return getKeyValues(null);
  }

  public ConcurrentHashMap<K, V> getKeyValues(Collection<K> keys) {
    LOGGER.debug(dbName + ": " + "getKeyValues ");
    ConcurrentHashMap<K, V> hashMap = new ConcurrentHashMap<K, V>();

    dbLock.readLock().lock();
    try (final Options options = new Options().setCreateIfMissing(true)) {
      // a factory method that returns a RocksDB instance
      File dbFile = new File(DB_PATH, dbName);
      try (final RocksDB db = RocksDB.openReadOnly(options, dbFile.getAbsolutePath())) {
        try (final RocksIterator iterator = db.newIterator(new ReadOptions())) {
          for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
            final K key = (K) deserialize(keyType, iterator.key());
            if (keys == null || keys.contains(key)) {
              final V value = (V) deserialize(valueType, iterator.value());
              hashMap.put(key, value);
            }
          }
          iterator.close();
          db.close();
        }
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    } finally {
      dbLock.readLock().unlock();
    }
    return hashMap;
  }

  // how we set the key name convention will affect the scan performance of db
  public List<K> scanPrefixKey(K prefixStr) {
    // the Options class contains a set of configurable DB options
    // that determines the behaviour of the database.

    List<K> list = new ArrayList<K>();

    dbLock.readLock().lock();
    try (final Options options = new Options().setCreateIfMissing(true)) {

      File dbFile = new File(DB_PATH, dbName);
      // a factory method that returns a RocksDB instance
      try (final RocksDB db = RocksDB.openReadOnly(options, dbFile.getAbsolutePath());
          ReadOptions readOptions = new ReadOptions()) {
        final byte[] prefixByte = serialize(prefixStr);
        /*
          An iterator that specifies a prefix (via ReadOptions) will use these bloom bits
          to avoid looking into data files that do not contain keys with the specified key-prefix.
        */

        final RocksIterator iterator = db.newIterator(readOptions.setPrefixSameAsStart(true));

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
        iterator.close();
        db.close();
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    } finally {
      dbLock.readLock().unlock();
    }
    return list;
  }

  public Iterator<K, V> iterator() {
    dbLock.readLock().lock();
    try (Options options = new Options().setCreateIfMissing(true)) {
      db = RocksDB.openReadOnly(options, new File(DB_PATH, dbName).getAbsolutePath());
      return new Iterator<K, V>(db.newIterator(new ReadOptions()));
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    } finally {
      dbLock.readLock().unlock();
    }
    return null;
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

  private static <T> T deserialize(Class<T> type, byte[] bytes) throws IOException {
    if (bytes == null || bytes.length == 0) {
      return null;
    }
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC);
    final ObjectReader r = mapper.readerFor(type);
    // enable one feature, disable another
    T value =
        r.with(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .without(DeserializationFeature.WRAP_EXCEPTIONS)
            .readValue(bytes);
    return value;
  }

  // This class wraps around rocksIterator to perform object serialization and deserialization
  public class Iterator<K, V> {
    public final RocksIterator iterator;

    protected Iterator(RocksIterator iterator) {
      iterator.seekToFirst();
      this.iterator = iterator;
    }

    public KeyValue<K, V> keyValue() {
      return new KeyValue<K, V>((K) getKeyOrValue(keyType), (V) getKeyOrValue(valueType));
    }

    public K key() {
      return (K) getKeyOrValue(keyType);
    }

    public V value() {
      return (V) getKeyOrValue(valueType);
    }

    public void close() {
      iterator.close();
      if (db != null) {
        db.close();
      }
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
        LOGGER.error(e.getMessage());
      }
      return null;
    }

    public void next() {
      iterator.next();
    }

    public boolean isValid() {
      return iterator.isValid();
    }
  }
}
