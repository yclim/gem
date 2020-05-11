package innohack.gem.web;

import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedFile;
import innohack.gem.entity.extractor.ExtractedRecords;
import innohack.gem.service.ExtractService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/extract")
public class ExtractController {

  @Autowired private ExtractService extractService;

  @PostMapping("/{groupId}")
  public List<ExtractedFile> extract(@PathVariable String groupId) {
    return extractService.extract(groupId);
  }
  
  @PostMapping("/{groupId}/{filename}")
  public ExtractedRecords getExtractedRecords(@PathVariable String groupId, @PathVariable String filename) {
    return extractService.extractRecords(groupId);
  }
  
  @PutMapping("/config/{groupId}")
  public ExtractConfig updateExtractConfig(@PathVariable String groupId, @RequestBody ExtractConfig config) {
    return extractService.updateExtractConfig(groupId, config);
  }
  
}
