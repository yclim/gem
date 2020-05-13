package innohack.gem.web;

import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedFile;
import innohack.gem.entity.extractor.ExtractedRecords;
import innohack.gem.service.ExtractService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/extract")
public class ExtractController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExtractController.class);

  @Autowired private ExtractService extractService;

  @PostMapping("/{groupId}")
  public List<ExtractedFile> extract(@PathVariable int groupId) throws Exception {
    return extractService.extract(groupId);
  }

  @GetMapping("/{groupId}")
  public List<ExtractedFile> getFilesCount(@PathVariable int groupId) {
    return extractService.getExtractedFiles(groupId);
  }

  @PostMapping("/{groupId}/{filename}")
  public ExtractedRecords getExtractedRecords(
      @PathVariable int groupId, @PathVariable String filename) {
    return extractService.getExtractedRecords(groupId, filename);
  }

  @PutMapping("/config/{groupId}")
  public ExtractConfig updateExtractConfig(
      @PathVariable int groupId, @RequestBody ExtractConfig config) {
    return extractService.updateExtractConfig(groupId, config);
  }

  @GetMapping("/config/{groupId}")
  public ExtractConfig getExtractConfig(@PathVariable int groupId) {
    return extractService.getExtractConfig(groupId);
  }
}
