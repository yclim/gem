package innohack.gem.web;

import innohack.gem.core.entity.extract.ExtractConfig;
import innohack.gem.core.entity.extract.ExtractedFile;
import innohack.gem.core.entity.extract.ExtractedRecords;
import innohack.gem.core.extract.AbstractExtractor;
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
import org.springframework.web.bind.annotation.RequestParam;
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

  @PostMapping("/")
  public ExtractedRecords getExtractedRecords(
      @RequestParam Integer groupId, @RequestParam String absolutePath) {
    return extractService.getExtractedRecords(groupId, absolutePath);
  }

  @PutMapping("/config/{groupId}")
  public ExtractConfig updateExtractConfig(
      @PathVariable int groupId, @RequestBody ExtractConfig config) {
    return extractService.updateExtractConfig(groupId, config);
  }

  @GetMapping("/config/{groupId}")
  public ExtractConfig getExtractConfig(@PathVariable int groupId) throws Exception {
    ExtractConfig config = extractService.getExtractConfig(groupId);
    if (config == null) {
      config = extractService.createExtractConfig(groupId);
    }
    return config;
  }

  @GetMapping("/extractorTemplates")
  public List<AbstractExtractor> getExtractorTemplates() throws Exception {
    return extractService.getExtractorTemplates();
  }
}
