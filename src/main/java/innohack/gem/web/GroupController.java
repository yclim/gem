package innohack.gem.web;

import java.util.Collection;

import innohack.gem.entity.GEMFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import innohack.gem.entity.Group;
import innohack.gem.service.GroupService;

@RestController
@RequestMapping("/api/group")
public class GroupController {
	
	@Autowired
	private GroupService groupService;

	// get all group
	@GetMapping
	public Collection<Group> getGroups() {
		return groupService.getGroups();
	}

	// get group by groupName
	@RequestMapping
	public Group getGroup(@RequestBody String groupName) {
		return groupService.getGroup(groupName);
	}

	// delete group by groupName
	@RequestMapping(method = RequestMethod.DELETE)
	public ResponseEntity<Object> deleteGroup(@RequestBody String groupName) {
		if(groupService.deleteGroup(groupName)){
			return new ResponseEntity<>("", HttpStatus.OK);
		}else{
			return new ResponseEntity<>("", HttpStatus.EXPECTATION_FAILED);
		}
	}
	// save group
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Object> saveGroup( @RequestBody Group group) {
		if(groupService.saveGroup(group)){
			return new ResponseEntity<>("", HttpStatus.OK);
		}else{
			return new ResponseEntity<>("", HttpStatus.EXPECTATION_FAILED);
		}
	}

	// get files that matches the group
	@RequestMapping("/files")
	public Collection<GEMFile> getMatchFile(@RequestBody String groupName) {
		return groupService.getGroup(groupName).getMatches();
	}
}
