package innohack.gem.service;

import innohack.gem.dao.IGEMFileDao;
import innohack.gem.dao.IGroupDao;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.Group;
import innohack.gem.service.event.EventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    }

    // check the group against all the files to see if any file matches it
    @Override
    public void onNewGroup(Group group) {
    }


}
