package innohack.gem.service;

import innohack.gem.dao.IGEMFileDao;
import innohack.gem.dao.IGroupDao;
import innohack.gem.dao.IMatchFileDao;
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
  @Autowired IGroupDao groupDao;
  @Autowired IGEMFileDao gemFileDao;
  @Autowired IMatchFileDao matchFileDao;

  private static Map<String, MatchFileGroup> matchFileGroupTable;
  private static Map<String, MatchFileRule> matchFileRuleTable;
  private List<MatchFileGroup> filesWithConflictMatch;
  private List<MatchFileGroup> filesWithoutMatch;

  public MatchService() {}

  public static Map<String, MatchFileGroup> getMatchFileGroupTable() {
    return matchFileGroupTable;
  }

  public static Map<String, MatchFileRule> getMatchFileRuleTable() {
    return matchFileRuleTable;
  }

  public synchronized void onUpdateEvent(Object o) {
    if (matchFileGroupTable == null) {
      matchFileGroupTable = matchFileDao.getMatchGroup();
    }
    if (matchFileRuleTable == null) {
      matchFileRuleTable = matchFileDao.getMatchRule();
    }
    if (o instanceof GEMFile) {
      onUpdateFile((GEMFile) o);
    }
    if (o instanceof Group) {
      onUpdateGroupRule((Group) o);
    }
    filesWithConflictMatch = null;
    filesWithoutMatch = null;
    backupMatchFile();
  }

  private static Thread backupProcessThread;
  private static final long BACKUP_WAIT_TIME = 1000 * 60 * 2;

  private void backupMatchFile() {
    Runnable backupProcess =
        () -> {
          try {
            Thread.sleep(BACKUP_WAIT_TIME);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          matchFileDao.saveMatchGroup(matchFileGroupTable);
          matchFileDao.saveMatchRule(matchFileRuleTable);
          backupProcessThread = null;
        };
    if (backupProcessThread == null) {
      System.out.println("MatchFile backup called");
      backupProcessThread = new Thread(backupProcess);
      backupProcessThread.start();
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
    List<Rule> rules = group.getRules();
    if (rules != null && rules.size() > 0) {
      for (Rule r : rules) {
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
    } else {
      result = false;
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
    Map<String, Group> groupToSave = new HashMap();
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
            groupToSave.put(group.getName(), group);
          }
        } else {
          if (group.getMatchedFile().contains(fileKey)) {
            group.getMatchedFile().remove(fileKey);
            groupToSave.put(group.getName(), group);
          }
        }
      }
    } else {
      // on file removed
      // remove all the files stored in hash and groups'list
      for (Group group : groupList) {
        if (group.getMatchedFile().contains(fileKey)) {
          group.getMatchedFile().remove(fileKey);
          groupToSave.put(group.getName(), group);
        }
      }
      matchFileRuleTable.remove(fileKey);
      matchFileGroupTable.remove(fileKey);
    }
    groupDao.saveGroups(groupToSave);
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
    for (String fileKey : matchFileRuleTable.keySet()) {
      MatchFileRule matchFileRule = matchFileRuleTable.get(fileKey);
      for (Rule r : group.getRules()) {
        matchFileRule.getMatchRuleHashcode().remove(r.hashCode());
      }
      matchFileRuleTable.put(fileKey, matchFileRule);
    }

    for (String fileKey : matchFileGroupTable.keySet()) {
      MatchFileGroup matchFileGroup = matchFileGroupTable.get(fileKey);
      matchFileGroup.getMatchedGroupIds().remove(group.getGroupId());
      matchFileGroupTable.put(fileKey, matchFileGroup);
    }
  }

  // This method calculate all the files with no match or more than 1 group match and update
  // filesWithoutMatch and filesWithConflictMatch
  public static final String NO_MATCH_TAG = "No matches";
  public static final String CONFLICT_TAG = "Conflicts";

  public synchronized Map<String, List<MatchFileGroup>> getMatchCount() {
    if (filesWithConflictMatch == null || filesWithoutMatch == null) {
      filesWithConflictMatch = new ArrayList<MatchFileGroup>();
      filesWithoutMatch = new ArrayList<MatchFileGroup>();
      Map<Integer, String> groupIdsMap = groupDao.getGroupIds();
      filesWithConflictMatch.clear();
      filesWithoutMatch.clear();
      for (GEMFile file : gemFileDao.getFiles()) {
        String fileKey = file.getAbsolutePath();
        MatchFileGroup matchFileGroup = matchFileGroupTable.get(fileKey);
        for (int grpId : matchFileGroup.getMatchedGroupIds()) {
          Set<String> groupNames = matchFileGroup.getMatchedGroupNames();
          if (groupNames == null) {
            groupNames = new HashSet();
          }
          groupNames.add(groupIdsMap.get(grpId));
          matchFileGroup.setMatchedGroupNames(groupNames);
        }
        if (matchFileGroup != null && matchFileGroup.getMatchedGroupIds().size() > 1) {
          filesWithConflictMatch.add(matchFileGroup);
        }
        if (matchFileGroup == null || (matchFileGroup.getMatchedGroupIds().size() == 0)) {
          filesWithoutMatch.add(matchFileGroup);
        }
      }
    }
    Map<String, List<MatchFileGroup>> result = new HashMap<>();
    result.put(NO_MATCH_TAG, filesWithoutMatch);
    result.put(CONFLICT_TAG, filesWithConflictMatch);
    return result;
  }

  public List<MatchFileGroup> getFilesWithConflictMatch() {
    return filesWithConflictMatch;
  }

  public List<MatchFileGroup> getFilesWithoutMatch() {
    return filesWithoutMatch;
  }
}
