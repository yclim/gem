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
  public static HashMap<String, GEMFile> featureStore = new HashMap<String, GEMFile>();
  public static Set<String> fileTypeStore = new HashSet<String>();

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
      if (f.getName().toLowerCase().contains(filename.toLowerCase())) {
        l.add(f);
      }
    }
    return l;
  }

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

  // Get list of files from feature store. It should return the minimum; file name and directory
  @Override
  public Collection<GEMFile> getFiles() {
    Collection<GEMFile> l = Lists.newArrayList();
    for (GEMFile f : featureStore.values()) {
      l.add(new GEMFile(f.getName(), f.getDirectory()));
    }
    return l;
  }

  // Get list of local files from directory
  @Override
  public Collection<GEMFile> getLocalFiles(String directory) {
    File dir = new File(directory);
    Collection<GEMFile> resultList = Lists.newArrayList();
    File[] fList = dir.listFiles();
    for (File file : fList) {
      if (file.isFile()) {
        resultList.add(new GEMFile(file.getName(), file.getParent()));
      } else {
        resultList.addAll(getLocalFiles(file.getAbsolutePath()));
      }
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
