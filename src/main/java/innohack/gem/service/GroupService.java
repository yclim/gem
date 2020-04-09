package innohack.gem.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import innohack.gem.dao.IGroupDao;
import innohack.gem.entity.Group;

@Service
public class GroupService {

	@Autowired
	private IGroupDao groupDao;
	
	public Collection<Group> getGroups() {
		return groupDao.getGroups();
	}
	
}
