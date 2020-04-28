package innohack.gem.service;

import com.google.common.collect.Lists;
import innohack.gem.dao.IGEMFileDao;
import innohack.gem.dao.IGroupDao;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.rule.Group;
import innohack.gem.entity.rule.rules.Rule;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchService {
  private static ConcurrentHashMap<String, Collection<Group>> matchedFileGroupTable =
      new ConcurrentHashMap<String, Collection<Group>>();
  private static ConcurrentHashMap<String, HashMap<Rule, Boolean>> matchedFileRuleTable =
      new ConcurrentHashMap<String, HashMap<Rule, Boolean>>();

  private List<GEMFile> filesWithConflictMatch = new ArrayList<GEMFile>();
  private List<GEMFile> filesWithoutMatch = new ArrayList<GEMFile>();

  @Autowired IGroupDao groupDao;
  @Autowired IGEMFileDao gemFileDao;

  public static ConcurrentHashMap<String, Collection<Group>> getMatchedFileGroupTable() {
    return matchedFileGroupTable;
  }

  public static ConcurrentHashMap<String, HashMap<Rule, Boolean>> getMatchedFileRuleTable() {
    return matchedFileRuleTable;
  }

  public synchronized void onUpdateEvent(Object o) {
    if (o instanceof GEMFile) {
      onUpdateFile((GEMFile) o);
    }
    if (o instanceof Group) {
      onUpdateGroupRule((Group) o);
    }
  }

  private boolean checkMatching(Group group, GEMFile file) {
    boolean result = true;
    String fileKey = file.getAbsolutePath();
    HashMap<Rule, Boolean> matchedRuleTable;
    if (matchedFileRuleTable.get(fileKey) == null) {
      matchedRuleTable = new HashMap<Rule, Boolean>();
    } else {
      matchedRuleTable = matchedFileRuleTable.get(fileKey);
    }

    for (Rule r : group.getRules()) {
      if (matchedRuleTable.get(r) == null) {
        result = r.check(file);
        matchedRuleTable.put(r, result);
        if (result == false) {
          break;
        }
      }
    }
    matchedFileRuleTable.put(fileKey, matchedRuleTable);

    Collection<Group> grps = matchedFileGroupTable.get(fileKey);
    if (grps == null) {
      grps = Lists.newArrayList();
    }
    if (result) {
      if (!grps.contains(group)) {
        grps.add(group);
      }
    } else {
      grps.remove(group);
    }
    matchedFileGroupTable.put(fileKey, grps);
    return result;
  }

  private void onUpdateFile(GEMFile updatedFile) {
    List<Group> groupList = groupDao.getGroups();
    GEMFile storedFile = gemFileDao.getFileByAbsolutePath(updatedFile.getAbsolutePath());
    if (storedFile != null) {
      // on new file added
      for (Group group : groupList) {
        if (checkMatching(group, storedFile)) {
          if (!group.getMatchedFile().contains(storedFile)) {
            group.getMatchedFile().add(storedFile);
          }
        } else {
          group.getMatchedFile().remove(storedFile);
        }
      }
    } else {
      // on file removed
      for (Group group : groupList) {
        group.getMatchedFile().remove(updatedFile);
      }
      String fileKey = updatedFile.getAbsolutePath();
      matchedFileRuleTable.remove(fileKey);
      matchedFileGroupTable.remove(fileKey);
    }
  }

  private void onUpdateGroupRule(Group updatedGroupRule) {
    List<GEMFile> gemFileList = gemFileDao.getFiles();
    Group storedGroupRule = groupDao.getGroup(updatedGroupRule.getName());

    if (storedGroupRule != null) {
      // on new or update grouprule
      for (GEMFile gemFile : gemFileList) {
        if (checkMatching(storedGroupRule, gemFile)) {
          if (!storedGroupRule.getMatchedFile().contains(gemFile)) {
            storedGroupRule.getMatchedFile().add(gemFile);
          }
        } else {
          storedGroupRule.getMatchedFile().remove(gemFile);
        }
      }
    } else {
      // on grouprule removed
      removeAllAssociatedRuleFromCache(updatedGroupRule);
    }
  }

  private void removeAllAssociatedRuleFromCache(Group group) {
    for (HashMap<Rule, Boolean> map : matchedFileRuleTable.values()) {
      for (Rule r : group.getRules()) {
        map.remove(r);
      }
    }
    for (Collection<Group> groupList : matchedFileGroupTable.values()) {
      groupList.remove(group);
    }
  }

  public void calculateAbnormalMatchCount() {
    filesWithConflictMatch.clear();
    filesWithoutMatch.clear();
    for (GEMFile file : gemFileDao.getFiles()) {
      String fileKey = file.getAbsolutePath();
      if (matchedFileGroupTable.get(fileKey).size() > 1) {
        filesWithConflictMatch.add(file);
      }
      if (matchedFileGroupTable.get(fileKey) == null
          || matchedFileGroupTable.get(fileKey).size() == 0) {
        filesWithoutMatch.add(file);
      }
    }
  }

  public List<GEMFile> getFilesWithConflictMatch() {
    return filesWithConflictMatch;
  }

  public List<GEMFile> getFilesWithoutMatch() {
    return filesWithoutMatch;
  }

}
