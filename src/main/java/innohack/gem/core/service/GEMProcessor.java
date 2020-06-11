package innohack.gem.core.service;

import com.beust.jcommander.internal.Lists;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.Project;
import innohack.gem.core.entity.extractor.ExtractConfig;
import innohack.gem.core.entity.extractor.ExtractedRecords;
import innohack.gem.core.entity.rule.Group;
import innohack.gem.core.entity.rule.rules.Rule;
import innohack.gem.service.extract.AbstractExtractor;
import java.util.ArrayList;
import java.util.List;

public class GEMProcessor {
  private GEMFile file;
  private Project project;

  public GEMProcessor(GEMFile file, Project project) {
    this.file = file;
    this.project = project;
  }

  public List<ExtractedRecords> run() {
    file.extract();
    List<Group> matchedGroup = classifyGroup();
    return extract(file, matchedGroup);
  }

  public List<Group> classifyGroup() {
    List<Group> matchedGroup = new ArrayList<Group>();
    project.getGroups().stream()
        .forEach(
            group -> {
              boolean match = true;
              List<Rule> rules = group.getRules();
              for (Rule rule : rules) {
                if (!rule.check(file)) {
                  match = false;
                  break;
                }
              }
              if (match) {
                matchedGroup.add(group);
              }
            });
    return matchedGroup;
  }

  public List<ExtractedRecords> extract(GEMFile file, List<Group> matchedGroup) {

    List<ExtractedRecords> results = Lists.newArrayList();
    matchedGroup.stream()
        .forEach(
            group -> {
              ExtractConfig config = group.getExtractConfig();
              AbstractExtractor extractor = config.getExtractor();
              ExtractedRecords records = null;
              try {
                records = extractor.extract(file, config);
              } catch (Exception e) {
                e.printStackTrace();
              }
              results.add(records);
            });
    return results;
  }
}
