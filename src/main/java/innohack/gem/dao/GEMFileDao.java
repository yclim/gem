package innohack.gem.dao;

import com.google.common.collect.Lists;
import innohack.gem.entity.GEMFile;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class GEMFileDao implements IGEMFileDao {
  private static HashMap<String, GEMFile> featureStore = new HashMap<>();
  private static String directoryStore = "";
  public static Set<String> fileTypeStore = new HashSet<String>();

  // Get current directory path where files uploaded
  /**
   * Find document in feature store by file name and directory
   *
   * @return directory path
   */
  @Override
  public String getCurrentDirectory() {
    return directoryStore;
  }

  /**
   * Find document in feature store by file name and directory
   *
   * @param filename document file name
   * @param directory directory where the file is
   * @return list of document metadata
   */
  // This method get file data from feature store
  @Override
  public Set<String> getFileTypes() {
    // TODO get file data from feature store
    return fileTypeStore;
  }
  // This method get file data from feature store
  @Override
  public GEMFile getFile(String filename, String directory) {
    // TODO get file data from feature store
    return featureStore.get(GEMFile.getAbsolutePath(filename, directory));
  }

  // This method get file data from feature store
  @Override
  public GEMFile getFileByAbsolutePath(String absolutePath) {
    // TODO get file data from feature store
    return featureStore.get(absolutePath);
  }

  @Override
  public Collection<GEMFile> findByName(String filename) {
    Collection<GEMFile> l = Lists.newArrayList();
    for (GEMFile f : featureStore.values()) {
      if (f.getFileName().contains(filename)) {
        l.add(f);
      }
    }
    return l;
  }
  /**
   * Find document metadata by document file extension
   *
   * @param extension document file extension
   * @return list of document metadata
   */
  @Override
  public Collection<GEMFile> findByExtension(String extension) {
    Collection<GEMFile> l = Lists.newArrayList();
    for (GEMFile f : featureStore.values()) {
      if (f.getExtension().toLowerCase().contains(extension.toLowerCase())) {
        l.add(f);
      }
    }
    return l;
  }

  /**
   * Retrieves all stored documents It should return the minimun; file name and directory
   *
   * @return list of metadata {@link GEMFile @GEMFile}
   */
  @Override
  public Collection<GEMFile> getFiles() {
    Collection<GEMFile> l = Lists.newArrayList();
    for (GEMFile f : featureStore.values()) {
      l.add(new GEMFile(f.getFileName(), f.getDirectory()));
    }
    return l;
  }

  // Get list of local files from directory
  @Override
  public Collection<GEMFile> getLocalFiles(String directory) {
    File dir = new File(directory);
    Collection<GEMFile> resultList = Lists.newArrayList();
    File[] fList = dir.listFiles();
    if(fList!=null){
      for (File file : fList) {
        if (file.isFile()) {
          resultList.add(new GEMFile(file.getName(), file.getParent()));
        } else {
          resultList.addAll(getLocalFiles(file.getAbsolutePath()));
        }
      }
      directoryStore = directory;
    }
    return resultList;
  }

  // save file to feature store
  @Override
  public void saveFile(GEMFile file) {
    // TODO Insert file into feature store
    fileTypeStore.add(file.getExtension());
    featureStore.put(file.getAbsolutePath(), file);
  }

  /*
  private GEMFile mockFile() {
  	GEMFile f1 = new GEMFile("AAAA.pdf","C:/Directory");
  	TikaFeature d1 = new TikaFeature();
  	d1.addMetadata("Compression Type","8 bits");
  	f1.addData(d1);
  	CsvFeature d2 = new CsvFeature();
  	f1.addData(d2);
  	return f1;
  }

  private GEMFile mockFile(String name, String dir) {
  	if(featureStore.size()>0) {
  		return featureStore.get(GEMFile.getAbsolutePath(name,dir));
  		//return featureStore.values().iterator().next();
  	}else{
  		GEMFile f1 = new GEMFile("AAAA.pdf", "C:/Directory");
  		TikaFeature d1 = new TikaFeature();
  		d1.addMetadata("Compression Type", "8 bits");
  		f1.addData(d1);
  		CsvFeature d2 = new CsvFeature();
  		f1.addData(d2);
  		return f1;
  	}
  }




  private Collection<GEMFile> mockFileList() {
  	Collection<GEMFile>  l = Lists.newArrayList();
  	if(featureStore.size()>0){
  		for (GEMFile f:featureStore.values()) {
  			l.add(new GEMFile( f.getName(),  f.getDirectory()));
  		}
  	}else{
  		for(int i =0 ;i<10; i ++) {
  			l.add(new GEMFile(String.format("Filename%d.pdf", i), "C:/Directory"));
  		}
  	}
  	return l;
  }*/
}
