package innohack.gem.dao;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.Group;

import java.util.Collection;

/**
 * Allow for injecting different DAO implementations...
 * e.g. in-memory, database, es, etc
 * @author TC
 *
 */
public interface IGEMFileDao {

	//TODO get files from folder path
	Collection<GEMFile> getFiles(String path);

	//TODO get all files from store
	Collection<GEMFile> getFiles();

	//TODO get file from data store
	GEMFile getFile(String path);

	void saveFiles(GEMFile files);
}
