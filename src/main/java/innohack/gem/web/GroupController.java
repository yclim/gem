package innohack.gem.web;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import innohack.gem.entity.Group;
import innohack.gem.service.GroupService;

@RestController
@RequestMapping("/group")
public class GroupController {
	
	@Autowired
	private GroupService groupService;

	@GetMapping
	public Collection<Group> getGroups() {
		return groupService.getGroups();
	}
	
}
