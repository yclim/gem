package innohack.gem.service.event;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.rule.Group;
import innohack.gem.service.MatcherService;

public abstract class EventListener {
  public enum Event {
    NEW_FILE,
    NEW_GROUP
  }

  private static EventListener[] listeners = {MatcherService.getInstance()};

  public static EventListener[] getListeners() {
    return listeners;
  }

  public void newEvent(Event event, Object object) {
    if (event.equals(Event.NEW_FILE) && object instanceof GEMFile) {
      onNewFile((GEMFile) object);
    }
    if (event.equals(Event.NEW_GROUP) && object instanceof Group) {
      onNewGroup((Group) object);
    }
  }

  public abstract void onNewFile(GEMFile fileChange);

  public abstract void onNewGroup(Group group);
}
