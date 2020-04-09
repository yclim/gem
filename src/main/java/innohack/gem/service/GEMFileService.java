package innohack.gem.service;

import innohack.gem.dao.BasicGEMFileDao;
import innohack.gem.dao.IGEMFileDao;
import innohack.gem.entity.GEMFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

import java.util.Collection;

@Service
public class GEMFileService {

	@Autowired
	private IGEMFileDao gemDao;

	public GEMFile getFile(String filePath){
		//TODO get file base on filepath and return
		return gemDao.getFile(filePath);
	}
	public Collection<GEMFile> syncFiles(String folderPath){
		//TODO get files and return file list
		Collection<GEMFile> filelist = gemDao.getFiles(folderPath);

		//TODO at the same time start processing the file at backend and store result
		DataRetrievalService dataRetrievalThread = new DataRetrievalService(filelist);
		Thread thread = new Thread(dataRetrievalThread);
		thread.start();

		return filelist;
	}

}
