package innohack.gem.web;

import innohack.gem.entity.GEMFile;
import innohack.gem.service.GEMFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/file")
public class GEMFileController {
	
	@Autowired
	private GEMFileService fileService;

	//get file by directory and filename
	@GetMapping("/findByNameAndDir")
	public GEMFile getFile(@RequestParam(name = "filename") String filename, @RequestParam(name = "directory") String directory) {
		return fileService.getFile(filename, directory);
	}
	//get file by absolute path
	@GetMapping("/findByAbsolutePath")
	public GEMFile getFileByAbsolutePath(@RequestParam(name = "absolutePath") String absolutePath) {
		return fileService.getFileByAbsolutePath(absolutePath);
	}

	/**
	 * Retrieves metadata for all uploaded documents
	 * @return list of metadata {@link GEMFile @DocumentMetadata}
	 */
	//get list of files from feature store
	@GetMapping("/list")
	public Collection<GEMFile> getFileList() {
		return fileService.getFileList();
	}

	//get list of files and perform data extraction
	@GetMapping("/sync")
	public Collection<GEMFile> sync(@RequestParam(name = "directory") String directory) {
		return fileService.syncFiles(directory);
	}

	/**
	 * Retrieves list of document metadata associated with given document name
	 * @param name document name
	 * @return list of metadata {@link GEMFile @DocumentMetadata}
	 */
	@RequestMapping("/findByName/{name:.+}")
	public Collection<GEMFile> findByName(@PathVariable String name) {
		return fileService.findByName(name);
	}

	/**
	 * Retrieves list of document metadata with given document file extension
	 * @param extension file name extension
	 * @return list of metadata {@link GEMFile @DocumentMetadata}
	 */
	@RequestMapping("/findByExtension/{extension}")
	public Collection<GEMFile> findByExtension(@PathVariable String extension) {
		return fileService.findByExtension(extension);
	}

}
