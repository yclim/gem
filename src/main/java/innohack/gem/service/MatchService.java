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
    // get rule hashmap of the file. rule hash map contains the result of rule checks on the file
    if (matchedFileRuleTable.get(fileKey) == null) {
      matchedRuleTable = new HashMap<Rule, Boolean>();
    } else {
      matchedRuleTable = matchedFileRuleTable.get(fileKey);
    }
    // for each rule in the group, check against the rule hashmap for previously checked result
    for (Rule r : group.getRules()) {
      if (matchedRuleTable.get(r) == null) {
        // when no result of previously checked file and rule, do a check now and store into rule
        // hashmap
        result = r.check(file);
        matchedRuleTable.put(r, result);
      } else {
        // when there is a result of rule checked previously inside rule hashmap,
        result = matchedRuleTable.get(r);
      }
      // break when one of the rule check against the file is false.
      if (result == false) {
        break;
      }
    }
    // save the rule hashmap
    matchedFileRuleTable.put(fileKey, matchedRuleTable);

    // FileGroupHashMap that contains all the file and its matched group
    // get the list of groups that match the file and update it
    Collection<Group> grps = matchedFileGroupTable.get(fileKey);
    if (grps == null) {
      grps = Lists.newArrayList();
    }
    if (result) {
      if (!grps.contains(group)) {
        // when group is not in the list and its file matched against the group. add the group to
        // the list
        grps.add(group);
      }
    } else {
      // when file does not match the group, remove the file from the list.
      grps.remove(group);
    }
    // save the matched group
    matchedFileGroupTable.put(fileKey, grps);
    return result;
  }

  private void onUpdateFile(GEMFile updatedFile) {
    List<Group> groupList = groupDao.getGroups();
    GEMFile storedFile = gemFileDao.getFileByAbsolutePath(updatedFile.getAbsolutePath());
    if (storedFile != null) {
      // on new file added
      for (Group group : groupList) {
        // perform matching, if file match the group, add the file to the group's list else remove
        // it
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
      // remove all the files stored in hash and groups'list
      String fileKey = updatedFile.getAbsolutePath();
      for (Group group : groupList) {
        group.getMatchedFile().remove(updatedFile);
      }
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
        // perform matching, if file match the group, add the file to the group's list else remove
        // it
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

  // remove all the grouprule from the hashmap
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

  // This method calculate all the files with no match or more than 1 group match and update
  // filesWithoutMatch and filesWithConflictMatch
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
