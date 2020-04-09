package innohack.gem.dao;

import com.google.common.collect.Lists;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.gem.data.CsvData;
import innohack.gem.entity.gem.data.TikaData;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * A basic group dao using everything in-memory
 * 
 * @author TC
 *
 */
@Repository
public class BasicGEMFileDao implements IGEMFileDao {
	@Override
	public Collection<GEMFile> getFiles(String path) {
		return mockGroups();
	}

	@Override
	public Collection<GEMFile> getFiles() {
		return mockGroups();
	}

	@Override
	public GEMFile getFile(String path) {
		return null;
	}

	@Override
	public void saveFiles(GEMFile files) {

	}

	private Collection<GEMFile> mockGroups() {
		GEMFile f1 = new GEMFile();
		TikaData d1 = new TikaData();
		d1.getMetadata().put("Compression Type","8 bits");
		f1.getData().add(d1);

		GEMFile f2 = new GEMFile();
		CsvData d2 = new CsvData();
		String header []={"A","B","C"};
		d2.setHeaders(header);
		String data [][]={{"1","2","3"},{"1","2","3"}};
		d2.setData(data);
		f2.getData().add(d2);

		return Lists.newArrayList(f1, f2);
	}
}
