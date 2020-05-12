package innohack.gem.entity.match;

import com.beust.jcommander.internal.Sets;
import java.util.HashSet;
import java.util.Set;

public class MatchFileGroup {
  private String filePath;
  private Set<Integer> matchedGroupIds;

  private Set<String> matchedGroupNames;

  public MatchFileGroup() {
    matchedGroupIds = new HashSet<>();
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public Set<Integer> getMatchedGroupIds() {
    if (matchedGroupIds == null) {
      matchedGroupIds = Sets.newHashSet();
    }
    return matchedGroupIds;
  }

  public void setMatchedGroupIds(Set<Integer> matchedGroupIds) {
    this.matchedGroupIds = matchedGroupIds;
  }

  public int getGroupMatchedCount() {
    return matchedGroupIds.size();
  }

  public Set<String> getMatchedGroupNames() {
    if (matchedGroupNames == null) {
      matchedGroupNames = Sets.newHashSet();
    }
    return matchedGroupNames;
  }

  public void setMatchedGroupNames(Set<String> matchedGroupNames) {
    this.matchedGroupNames = matchedGroupNames;
  }
}
