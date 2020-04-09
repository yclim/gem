package innohack.gem.service;

import com.google.common.collect.Lists;
import innohack.gem.dao.IGEMFileDao;
import innohack.gem.dao.IGroupDao;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.Group;
import innohack.gem.entity.Rule;
import innohack.gem.entity.gem.data.AbstractData;
import innohack.gem.service.event.EventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collection;
@Service
public class MatcherService extends EventListener {

    static MatcherService instance = null;
    // singleton method, used by eventlistener to ensure at anyone point only 1 instance of MatcherService is listening to event
    public static MatcherService getInstance(){
        if(instance==null){
            instance = new MatcherService();
        }
       return  instance;
    }

    @Autowired
    private IGEMFileDao gemDao;
    @Autowired
    private IGroupDao groupDao;

    // check for match for the new file against all groups
    @Override
    public void onNewFile(GEMFile fileChange) {
        Collection<Group> groups = groupDao.getGroups();
        Collection<Group> matchedGroups = matchGroups(fileChange, groups);
        for (Group g: matchedGroups ) {
            groupDao.saveGroup(g);
        }
    }

    // check the group against all the files to see if any file matches it
    @Override
    public void onNewGroup(Group group) {
        Collection<GEMFile> files = gemDao.getFiles();
        for (GEMFile f: files ) {
            Group matchGroup = matchGroup(f, group);
            if(matchGroup!=null){
                groupDao.saveGroup(matchGroup);
            }
        }
    }

    // return list of groups that matches any data in the file
    public Collection<Group> matchGroups(GEMFile file, Collection<Group> groups){
        Collection<Group> matchedGroups = Lists.newArrayList();
        for(Group g : groups){
            Group matchedGroup = matchGroup(file, g);
            if(matchedGroup!=null){
                matchedGroups.add(matchedGroup);
                matchedGroup.getMatches().add(file); //add file to the group
            }
        }
        return matchedGroups;
    }

    // return group that matches any data in the file
    public Group matchGroup(GEMFile file, Group group){
        Collection<Rule> rules = group.getRules();
        Collection<Rule> matchedRules = matchRule(file, rules);
        if(matchedRules.size()==rules.size()){ // AND condition, all rules in group must match
            group.getMatches().add(file);
            return group;
        }
        return null;
    }

    // return list of rules if they matches any data in the file
    public Collection<Rule> matchRule(GEMFile file, Collection<Rule> rules){
        Collection<Rule> matchedRules = Lists.newArrayList();
        for(Rule r : rules){
            Rule matched = matchRule(file, r);
            if(matched!=null){
                matchedRules.add(matched);
            }
        }
        return rules;
    }

    // return rule if it matches any data in the file
    public Rule matchRule(GEMFile file, Rule rule){
        for(AbstractData d : file.getData()){
            if(d.getTarget().equals(rule.getTarget())){
                if(d.matchRule(rule)!=null){
                    return rule;
                }
            }
        }
        return null;
    }
}
