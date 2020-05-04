package innohack.gem.service;

import innohack.gem.dao.IGEMFileDao;
import innohack.gem.dao.IGroupDao;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.match.MatchFileGroup;
import innohack.gem.entity.match.MatchFileRule;
import innohack.gem.entity.rule.Group;
import innohack.gem.entity.rule.rules.Rule;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchService {
  static final String DB_NAME = "Match";

  /*
  private RocksDatabase matchFileGroupTableDb;
  private RocksDatabase matchFileRuleTableDb;

  public MatchService(){
    matchFileGroupTableDb = RocksDatabase.getInstance(DB_NAME+"_FileGroup", String.class, GEMFile.class);
    matchFileRuleTableDb = RocksDatabase.getInstance(DB_NAME+"_FileRule", String.class, Boolean.class);
  }
  */

  public HashMap<String, MatchFileGroup> matchFileGroupTable = new HashMap();
  public HashMap<String, MatchFileRule> matchFileRuleTable = new HashMap();
  private List<GEMFile> filesWithConflictMatch = new ArrayList<GEMFile>();
  private List<GEMFile> filesWithoutMatch = new ArrayList<GEMFile>();

  @Autowired IGroupDao groupDao;
  @Autowired IGEMFileDao gemFileDao;

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
    // get rule hashmap of the file. rule hash map contains the result of rule checks on the file
    Object o = matchFileRuleTable.get(fileKey);
    MatchFileRule matchFileRule = new MatchFileRule();
    HashMap<Integer, Boolean> ruleMatch = null;
    if (o != null) {
      matchFileRule = (MatchFileRule) o;
      matchFileRule.setFilePath(fileKey);
      ruleMatch = matchFileRule.getMatchRuleHashcode();
    }
    if (ruleMatch == null) {
      ruleMatch = new HashMap<Integer, Boolean>();
    }
    // for each rule in the group, check against the rule hashmap for previously checked result
    for (Rule r : group.getRules()) {
      if (ruleMatch.get(r.hashCode()) == null) {
        // when no result of previously checked file and rule, do a check now and store into rule
        // hashmap
        result = r.check(file);
        ruleMatch.put(r.hashCode(), result);
      } else {
        // when there is a result of rule checked previously inside rule hashmap,
        result = ruleMatch.get(r.hashCode());
      }
      // break when one of the rule check against the file is false.
      if (result == false) {
        break;
      }
    }
    matchFileRule.setMatchRuleHashcode(ruleMatch);
    // save the rule hashmap
    matchFileRuleTable.put(fileKey, matchFileRule);

    // FileGroupHashMap that contains all the file and its matched group
    // get the list of groups that match the file and update it

    o = matchFileGroupTable.get(fileKey);
    MatchFileGroup matchFileGroup = new MatchFileGroup();
    Set<Integer> matchGroupIds = null;
    if (o != null) {
      matchFileGroup = (MatchFileGroup) o;
      matchFileGroup.setFilePath(fileKey);
      matchGroupIds = matchFileGroup.getMatchedGroupIds();
    }

    if (matchGroupIds == null) {
      matchGroupIds = new HashSet();
    }
    if (result) {
      // when group is not in the list and its file matched against the group. add the group to
      // the list
      matchGroupIds.add(group.getGroupId());
    } else {
      // when file does not match the group, remove the file from the list.
      matchGroupIds.remove(group.getGroupId());
    }
    // save the matched group
    matchFileGroup.setMatchedGroupIds(matchGroupIds);
    matchFileGroupTable.put(fileKey, matchFileGroup);
    return result;
  }

  private void onUpdateFile(GEMFile updatedFile) {
    List<Group> groupList = groupDao.getGroups();
    String fileKey = updatedFile.getAbsolutePath();
    GEMFile storedFile = gemFileDao.getFileByAbsolutePath(fileKey);
    if (storedFile != null) {
      // on new file added
      for (Group group : groupList) {
        // perform matching, if file match the group, add the file to the group's list else remove
        // it
        if (checkMatching(group, storedFile)) {
          if (!group.getMatchedFile().contains(fileKey)) {
            group.getMatchedFile().add(fileKey);
            groupDao.saveGroup(group);
          }
        } else {
          if (group.getMatchedFile().contains(fileKey)) {
            group.getMatchedFile().remove(fileKey);
            groupDao.saveGroup(group);
          }
        }
      }
    } else {
      // on file removed
      // remove all the files stored in hash and groups'list
      for (Group group : groupList) {
        if (group.getMatchedFile().contains(fileKey)) {
          group.getMatchedFile().remove(fileKey);
          groupDao.saveGroup(group);
        }
      }
      matchFileRuleTable.remove(fileKey);
      matchFileGroupTable.remove(fileKey);
    }
  }

  private void onUpdateGroupRule(Group updatedGroupRule) {
    List<GEMFile> gemFileList = gemFileDao.getFiles();
    Group storedGroupRule = groupDao.getGroup(updatedGroupRule.getName());

    if (storedGroupRule != null) {
      // on new or update grouprule
      boolean groupUpdated = false;
      for (GEMFile gemFile : gemFileList) {
        String fileKey = gemFile.getAbsolutePath();
        // perform matching, if file match the group, add the file to the group's list else remove
        // it
        if (checkMatching(storedGroupRule, gemFile)) {
          if (!storedGroupRule.getMatchedFile().contains(fileKey)) {
            storedGroupRule.getMatchedFile().add(fileKey);
            groupUpdated = true;
          }
        } else {
          if (storedGroupRule.getMatchedFile().contains(fileKey)) {
            storedGroupRule.getMatchedFile().remove(fileKey);
            groupUpdated = true;
          }
        }
      }
      if (groupUpdated) {
        groupDao.saveGroup(storedGroupRule);
      }
    } else {
      // on grouprule removed
      removeAllAssociatedRuleFromCache(updatedGroupRule);
    }
  }

  // remove all the grouprule from the hashmap
  private void removeAllAssociatedRuleFromCache(Group group) {
    HashMap<String, MatchFileRule> matchFileRuleHashMap = matchFileRuleTable;
    for (String fileKey : matchFileRuleHashMap.keySet()) {
      MatchFileRule matchFileRule = matchFileRuleHashMap.get(fileKey);
      for (Rule r : group.getRules()) {
        matchFileRule.getMatchRuleHashcode().remove(r.hashCode());
      }
      matchFileRuleTable.put(fileKey, matchFileRule);
    }

    HashMap<String, MatchFileGroup> matchFileGroupHashMap = matchFileGroupTable;
    for (String fileKey : matchFileGroupHashMap.keySet()) {
      MatchFileGroup matchFileGroup = matchFileGroupHashMap.get(fileKey);
      matchFileGroup.getMatchedGroupIds().remove(group.getGroupId());
      matchFileGroupTable.put(fileKey, matchFileGroup);
    }
  }

  // This method calculate all the files with no match or more than 1 group match and update
  // filesWithoutMatch and filesWithConflictMatch
  public void calculateAbnormalMatchCount() {
    filesWithConflictMatch.clear();
    filesWithoutMatch.clear();
    for (GEMFile file : gemFileDao.getFiles()) {
      String fileKey = file.getAbsolutePath();
      MatchFileGroup matchFileGroup = matchFileGroupTable.get(fileKey);
      if (matchFileGroup != null && matchFileGroup.getMatchedGroupIds().size() > 1) {
        filesWithConflictMatch.add(file);
      }
      if (matchFileGroup == null || (matchFileGroup.getMatchedGroupIds().size() == 0)) {
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
