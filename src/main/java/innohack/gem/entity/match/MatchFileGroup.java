package innohack.gem.entity.match;

import java.util.HashSet;
import java.util.Set;

public class MatchFileGroup {
    private String filePath;
    private Set<Integer> matchedGroupIds;

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
        return matchedGroupIds;
    }

    public void setMatchedGroupIds(Set<Integer> matchedGroupIds) {
        this.matchedGroupIds = matchedGroupIds;
    }
}
