package innohack.gem.service;

import com.beust.jcommander.internal.Maps;
import com.beust.jcommander.internal.Sets;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.match.MatchFileGroup;
import innohack.gem.core.entity.match.MatchFileRule;
import innohack.gem.core.entity.rule.Group;
import innohack.gem.core.rules.Rule;
import innohack.gem.dao.IGEMFileDao;
import innohack.gem.dao.IGroupDao;
import innohack.gem.dao.IMatchFileDao;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchService {
  @Autowired private IGroupDao groupDao;
  @Autowired private IGEMFileDao gemFileDao;
  @Autowired private IMatchFileDao matchFileDao;

  private static Map<String, MatchFileGroup> matchFileGroupTable;
  private static Map<String, MatchFileRule> matchFileRuleTable;
  private List<MatchFileGroup> filesWithConflictMatch;
  private List<MatchFileGroup> filesWithoutMatch;

  public MatchService() {}

  public static Map<String, MatchFileRule> getMatchFileRuleTable() {
    return matchFileRuleTable;
  }

  public static Map<String, MatchFileGroup> getMatchFileGroupTable() {
    return matchFileGroupTable;
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
    // backupMatchFile();
  }

  private static Thread backupProcessThread;
  private static final long BACKUP_WAIT_TIME = 3000 * 1 * 1;

  public void backupMatchFile() {
    Runnable backupProcess =
        () -> {
          try {
            Thread.sleep(BACKUP_WAIT_TIME);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          backupProcessThread = null;
          matchFileDao.saveMatchGroup(matchFileGroupTable);
          matchFileDao.saveMatchRule(matchFileRuleTable);
        };
    if (backupProcessThread == null) {
      backupProcessThread = new Thread(backupProcess);
      backupProcessThread.start();
    }
  }

  private boolean checkMatching(Group group, GEMFile file) {
    boolean result = true;
    String fileKey = file.getAbsolutePath();
    // get rule hashmap of the file. rule hash map contains the result of rule checks on the file

    MatchFileRule matchFileRule =
        matchFileRuleTable.computeIfAbsent(
            fileKey,
            k -> {
              MatchFileRule f = new MatchFileRule();
              f.setDirectory(file.getDirectory());
              f.setFileName(file.getFileName());
              return f;
            });

    HashMap<Integer, Boolean> ruleResultMap = matchFileRule.getRuleResultMap();

    // for each rule in the group, check against the rule hashmap for previously checked result
    // when no result of previously checked file and rule, do a check now and store into rule
    List<Rule> rules = group.getRules();
    if (rules != null) {
      for (Rule r : rules) {
        if (!ruleResultMap.containsKey(r.hashCode())) {
          // file from parameter only provides filename and path.
          // We need the full GEMFile with features
          GEMFile completeGemFile = gemFileDao.getFileByAbsolutePath(file.getAbsolutePath());
          result = r.check(completeGemFile);
          ruleResultMap.put(r.hashCode(), result);
        } else {
          result = ruleResultMap.get(r.hashCode());
        }
        if (result == false) {
          break;
        }
      }
    } else {
      result = false;
    }
    matchFileRule.setRuleResultMap(ruleResultMap);
    // save the rule hashmap
    matchFileRuleTable.put(fileKey, matchFileRule);

    MatchFileGroup matchFileGroup =
        matchFileGroupTable.computeIfAbsent(
            fileKey,
            k -> {
              MatchFileGroup f = new MatchFileGroup();
              f.setDirectory(file.getDirectory());
              f.setFileName(file.getFileName());
              return f;
            });

    // FileGroupHashMap that contains all the file and its matched group
    // get the list of groups that match the file and update it
    Set<Integer> matchGroupIds = matchFileGroup.getMatchedGroupIds();
    if (result) {
      matchGroupIds.add(group.getGroupId());
    } else {
      matchGroupIds.remove(group.getGroupId());
    }
    matchFileGroup.setMatchedGroupIds(matchGroupIds);
    matchFileGroupTable.put(fileKey, matchFileGroup);
    return result;
  }

  private void onUpdateFile(GEMFile updatedFile) {
    Map<String, Group> groupToSave = Maps.newHashMap();
    List<Group> groupList = groupDao.getGroups();
    GEMFile fileKey = new GEMFile(updatedFile.getAbsolutePath());
    GEMFile storedFile = gemFileDao.getFileByAbsolutePath(updatedFile.getAbsolutePath());
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
      matchFileRuleTable.remove(fileKey.getAbsolutePath());
      matchFileGroupTable.remove(fileKey.getAbsolutePath());
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
        GEMFile fileKey = new GEMFile(gemFile.getAbsolutePath());
        // perform matching, if file match the group, add the file to the group's list else remove
        // it
        if (checkMatching(storedGroupRule, gemFile)) {
          if (!storedGroupRule.getMatchedFile().contains(fileKey)) {
            fileKey.setErrorMessage(gemFile.getErrorMessage());
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
        updatedGroupRule.setMatchedFile(storedGroupRule.getMatchedFile());
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
        matchFileRule.getRuleResultMap().remove(r.hashCode());
      }
      matchFileRuleTable.put(fileKey, matchFileRule);
    }

    for (String fileKey : matchFileGroupTable.keySet()) {
      MatchFileGroup matchFileGroup = matchFileGroupTable.get(fileKey);
      matchFileGroup.getMatchedGroupIds().remove(group.getGroupId());
      // matchFileGroup.getMatchedGroupNames().remove(groupIdsMap.get(group.getGroupId()));
      matchFileGroupTable.put(fileKey, matchFileGroup);
    }
  }

  // This method calculate all the files with no match or more than 1 group match and update
  // filesWithoutMatch and filesWithConflictMatch
  public static final String NO_MATCH_TAG = "noMatch";
  public static final String CONFLICT_TAG = "conflict";

  public synchronized Map<String, List<MatchFileGroup>> getMatchCount() {
    if (filesWithConflictMatch == null || filesWithoutMatch == null) {
      filesWithConflictMatch = new ArrayList<MatchFileGroup>();
      filesWithoutMatch = new ArrayList<MatchFileGroup>();
      Map<Integer, String> groupIdsMap = groupDao.getGroupIds();
      for (GEMFile file : gemFileDao.getFiles()) {
        String fileKey = file.getAbsolutePath();
        if (matchFileGroupTable == null) {
          matchFileGroupTable = matchFileDao.getMatchGroup();
        }
        MatchFileGroup matchFileGroup = matchFileGroupTable.get(fileKey);
        Set<String> groupNames = Sets.newHashSet();
        if (matchFileGroup != null) {
          for (int grpId : matchFileGroup.getMatchedGroupIds()) {
            if (groupNames == null) {
              groupNames = Sets.newHashSet();
            }
            groupNames.add(groupIdsMap.get(grpId));
          }
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

  public int[] getFileStat() {
    Map<String, List<MatchFileGroup>> counts = getMatchCount();
    int[] intArr = new int[2];
    intArr[0] = counts.get(NO_MATCH_TAG).size();
    intArr[1] = counts.get(CONFLICT_TAG).size();
    return intArr;
  }
}
