package innohack.gem.service;

import java.util.Collection;

import innohack.gem.service.event.EventListener;
import innohack.gem.service.event.NewEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import innohack.gem.dao.IGroupDao;
import innohack.gem.entity.Group;

@Service
public class GroupService extends NewEvent {

	@Autowired
	private IGroupDao groupDao;

	public Collection<Group> getGroups() {
		return groupDao.getGroups();
	}
	public Group getGroup(String groupName) {
		return groupDao.getGroup(groupName);
	}
	public boolean deleteGroup(String groupName) {
		return groupDao.deleteGroup(groupName);
	}
	public boolean saveGroup(Group group) {
		boolean result =groupDao.saveGroup(group);
		if(result)
			newEvent(EventListener.Event.NEW_GROUP,group);
		return result;
	}
	
}
