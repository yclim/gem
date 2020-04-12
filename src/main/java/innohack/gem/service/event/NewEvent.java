package innohack.gem.service.event;

public abstract class NewEvent {
  protected static void newEvent(EventListener.Event event, Object object) {
    for (EventListener e : EventListener.getListeners()) {
      e.newEvent(event, object);
    }
  }
}
