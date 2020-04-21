package innohack.gem.web;

import innohack.gem.entity.rule.Group;
import innohack.gem.service.GroupService;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group")
public class GroupController {

  @Autowired private GroupService groupService;

  // get all group
  @GetMapping("/list")
  public Collection<Group> getGroups() {
    return groupService.getGroups();
  }

  // get group by groupName
  @RequestMapping
  public Group getGroup(@RequestParam(name = "name") String name) {
    return groupService.getGroup(name);
  }

  // delete group by groupName
  @RequestMapping(method = RequestMethod.DELETE)
  public ResponseEntity<Object> deleteGroup(@RequestParam(name = "name") String name) {
    if (groupService.deleteGroup(name)) {
      return new ResponseEntity<>("", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("", HttpStatus.EXPECTATION_FAILED);
    }
  }
  // save group
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Object> saveGroup(@RequestBody Group group) {
    if (groupService.saveGroup(group)) {
      return new ResponseEntity<>("", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("", HttpStatus.EXPECTATION_FAILED);
    }
  }
}
