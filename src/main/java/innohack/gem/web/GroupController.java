package innohack.gem.web;

import innohack.gem.entity.rule.Group;
import innohack.gem.service.GroupService;
import innohack.gem.service.MatchService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/group")
public class GroupController {

  @Autowired private GroupService groupService;
  @Autowired private MatchService matchService;

  @PostMapping("/import")
  public List<Group> importProject(@RequestParam("file") MultipartFile file) throws IOException {
    return groupService.importProject(file.getBytes());
  }

  @GetMapping("/export")
  public void exportProject(
      @RequestParam(name = "name") String name,
      @RequestParam(name = "version") String version,
      HttpServletRequest request,
      HttpServletResponse response)
      throws IOException {
    byte[] project = groupService.exportProject(name, version);
    response.setContentType("application/json");
    response.addHeader("Content-Disposition", "attachment; filename=export.json");
    IOUtils.copy(new ByteArrayInputStream(project), response.getOutputStream());
    response.getOutputStream().flush();
  }

  @GetMapping("/export/spec")
  public byte[] getSpec(
      @RequestParam(name = "name", required = false) String name,
      @RequestParam(name = "version", required = false) String version)
      throws IOException {
    byte[] project = groupService.exportProject(name, version);
    return project;
  }

  // get all group
  @GetMapping("/list")
  public List<Group> getGroups() {
    return groupService.getGroups();
  }

  // get unmatched and conflict count
  @GetMapping("/getFileStat")
  public int[] getFileStat() {
    return matchService.getFileStat();
  }

  // get group by groupName
  @RequestMapping("/name")
  public Group getGroup(@RequestParam(name = "name") String name) {
    return groupService.getGroup(name);
  }

  // get group by groupId
  @RequestMapping("/groupId")
  public Group getGroup(@RequestParam(name = "groupId") int groupId) {
    return groupService.getGroup(groupId);
  }

  // delete group by groupName
  @RequestMapping(path = "/name", method = RequestMethod.DELETE)
  public ResponseEntity<Object> deleteGroup(@RequestParam(name = "name") String name) {
    if (groupService.deleteGroup(name)) {
      return new ResponseEntity<>("", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("", HttpStatus.EXPECTATION_FAILED);
    }
  }
  // delete group by groupId
  @RequestMapping(path = "/groupId", method = RequestMethod.DELETE)
  public ResponseEntity<Object> deleteGroup(@RequestParam(name = "groupId") int groupId) {
    if (groupService.deleteGroup(groupId)) {
      return new ResponseEntity<>("", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("", HttpStatus.EXPECTATION_FAILED);
    }
  }

  // update groupName
  @RequestMapping(path = "/name", method = RequestMethod.PUT)
  public ResponseEntity<Object> updateGroupName(
      @RequestParam(name = "oldGroupName") String oldGroupName,
      @RequestParam(name = "newGroupName") String newGroupName) {
    if (groupService.updateGroupName(oldGroupName, newGroupName)) {
      return new ResponseEntity<>("", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("", HttpStatus.EXPECTATION_FAILED);
    }
  }
  // save group
  @RequestMapping(method = RequestMethod.POST)
  public Group saveGroup(@RequestBody Group group) {
    return groupService.saveGroup(group);
  }
}
