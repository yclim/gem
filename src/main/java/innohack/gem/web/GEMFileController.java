package innohack.gem.web;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.Group;
import innohack.gem.service.GEMFileService;
import innohack.gem.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/file")
public class GEMFileController {
	
	@Autowired
	private GEMFileService fileService;

	// getlist of files and perform data extraction
	@GetMapping("/sync")
	public Collection<GEMFile> sync(@RequestBody String folderPath) {
		return fileService.syncFiles(folderPath);
	}
	// get detail of single file from data store
	@GetMapping
	public GEMFile getFile(@RequestBody String filePath) {
		return fileService.getFile(filePath);
	}
}
